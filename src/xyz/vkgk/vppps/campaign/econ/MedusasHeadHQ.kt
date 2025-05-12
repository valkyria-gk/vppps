package xyz.vkgk.vppps.campaign.econ

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BattleAPI
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI
import com.fs.starfarer.api.campaign.econ.Industry
import com.fs.starfarer.api.campaign.econ.Industry.IndustryTooltipMode
import com.fs.starfarer.api.campaign.listeners.FleetEventListener
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory.PatrolType
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3
import com.fs.starfarer.api.impl.campaign.fleets.PatrolAssignmentAIV4
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.*
import com.fs.starfarer.api.impl.campaign.ids.Ranks
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel
import com.fs.starfarer.api.loading.VariantSource
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.util.Pair
import com.fs.starfarer.api.util.WeightedRandomPicker
import org.lwjgl.util.vector.Vector2f
import xyz.vkgk.vppps.LocalFactionStrings

class MedusasHeadHQ : BaseIndustry(), RouteFleetSpawner, FleetEventListener {
    protected var tracker: IntervalUtil = IntervalUtil(
        Global.getSettings().getFloat("averagePatrolSpawnInterval") * 0.7f,
        Global.getSettings().getFloat("averagePatrolSpawnInterval") * 1.3f
    )
    protected var returningPatrolValue: Float = 0.0f

    override fun isHidden(): Boolean {
        return this.market.getFactionId() != LocalFactionStrings.PERSEAN_FREE_DOMAIN
    }

    override fun isFunctional(): Boolean {
        return super.isFunctional() && this.market.getFactionId() == LocalFactionStrings.PERSEAN_FREE_DOMAIN
    }

    override fun apply() {
        super.apply(true)
        val size = this.market.getSize()
        this.demand("supplies", size - 1)
        this.demand("fuel", size - 1)
        this.demand("ships", size - 1)
        this.supply("crew", size)
        this.demand("hand_weapons", size)
        this.supply("marines", size)
        val deficit = this.getMaxDeficit(*arrayOf<String>("hand_weapons"))
        this.applyDeficitToProduction(1, deficit, *arrayOf<String>("marines"))
        this.modifyStabilityWithBaseMod()
        val memory = this.market.getMemoryWithoutUpdate()
        Misc.setFlagWithReason(memory, "\$patrol", this.getModId(), true, -1.0f)
        Misc.setFlagWithReason(memory, "\$military", this.getModId(), true, -1.0f)
        if (!this.isFunctional()) {
            this.supply.clear()
            this.unapply()
        }
    }

    override fun unapply() {
        super.unapply()
        val memory = this.market.getMemoryWithoutUpdate()
        Misc.setFlagWithReason(memory, "\$patrol", this.getModId(), false, -1.0f)
        Misc.setFlagWithReason(memory, "\$military", this.getModId(), false, -1.0f)
        this.unmodifyStabilityWithBaseMod()
    }

    override fun hasPostDemandSection(hasDemand: Boolean, mode: IndustryTooltipMode?): Boolean {
        return mode != IndustryTooltipMode.NORMAL || this.isFunctional()
    }

    override fun addPostDemandSection(tooltip: TooltipMakerAPI?, hasDemand: Boolean, mode: IndustryTooltipMode?) {
        if (mode != IndustryTooltipMode.NORMAL || this.isFunctional()) {
            this.addStabilityPostDemandSection(tooltip, hasDemand, mode)
        }
    }

    override fun getBaseStabilityMod(): Int {
        return 2
    }

    override fun getNameForModifier(): String? {
        return if (this.getSpec().getName().contains("HQ")) this.getSpec().getName() else Misc.ucFirst(
            this.getSpec().getName()
        )
    }

    override fun getStabilityAffectingDeficit(): Pair<String?, Int?>? {
        return this.getMaxDeficit(*arrayOf<String>("supplies", "fuel", "ships", "hand_weapons"))
    }

    override fun getCurrentImage(): String? {
        return super.getCurrentImage()
    }

    override fun isDemandLegal(com: CommodityOnMarketAPI?): Boolean {
        return true
    }

    override fun isSupplyLegal(com: CommodityOnMarketAPI?): Boolean {
        return true
    }

    override fun buildingFinished() {
        super.buildingFinished()
        this.tracker.forceIntervalElapsed()
    }

    override fun upgradeFinished(previous: Industry?) {
        super.upgradeFinished(previous)
        this.tracker.forceIntervalElapsed()
    }

    override fun advance(amount: Float) {
        super.advance(amount)
        if (!Global.getSector().getEconomy().isSimMode()) {
            if (this.isFunctional()) {
                val days = Global.getSector().getClock().convertToDays(amount)
                var spawnRate = 1.0f
                val rateMult =
                    this.market.getStats().getDynamic().getStat("combat_fleet_spawn_rate_mult").getModifiedValue()
                spawnRate *= rateMult
                var extraTime = 0.0f
                if (this.returningPatrolValue > 0.0f) {
                    val interval = this.tracker.getIntervalDuration()
                    extraTime = interval * days
                    this.returningPatrolValue -= days
                    if (this.returningPatrolValue < 0.0f) {
                        this.returningPatrolValue = 0.0f
                    }
                }

                this.tracker.advance(days * spawnRate + extraTime)
                if (this.tracker.intervalElapsed()) {
                    val sid = this.getRouteSourceId()
                    val light = this.getCount(PatrolType.FAST)
                    val medium = this.getCount(PatrolType.COMBAT)
                    val heavy = this.getCount(PatrolType.HEAVY)
                    val maxLight = 2
                    val maxMedium = 2
                    val maxHeavy = 2
                    val picker: WeightedRandomPicker<PatrolType?> = WeightedRandomPicker<PatrolType?>()
                    picker.add(PatrolType.HEAVY, (maxHeavy - heavy).toFloat())
                    picker.add(PatrolType.COMBAT, (maxMedium - medium).toFloat())
                    picker.add(PatrolType.FAST, (maxLight - light).toFloat())
                    if (picker.isEmpty()) {
                        return
                    }

                    val type = picker.pick() as PatrolType
                    val custom = MilitaryBase.PatrolFleetData(type)
                    val extra = OptionalFleetData(this.market)
                    extra.fleetType = type.getFleetType()
                    val route =
                        RouteManager.getInstance().addRoute(sid, this.market, Misc.genRandomSeed(), extra, this, custom)
                    val patrolDays = 35.0f + Math.random().toFloat() * 10.0f
                    route.addSegment(RouteSegment(patrolDays, this.market.getPrimaryEntity()))
                }
            }
        }
    }

    override fun reportAboutToBeDespawnedByRouteManager(route: RouteData?) {
    }

    override fun shouldRepeat(route: RouteData?): Boolean {
        return false
    }

    fun getCount(vararg types: PatrolType?): Int {
        var count = 0

        for (data in RouteManager.getInstance().getRoutesForSource(this.getRouteSourceId())) {
            if (data.getCustom() is MilitaryBase.PatrolFleetData) {
                val custom = data.getCustom() as MilitaryBase.PatrolFleetData

                for (type in types) {
                    if (type == custom.type) {
                        ++count
                        break
                    }
                }
            }
        }

        return count
    }

    override fun shouldCancelRouteAfterDelayCheck(route: RouteData?): Boolean {
        return false
    }

    override fun reportBattleOccurred(fleet: CampaignFleetAPI?, primaryWinner: CampaignFleetAPI?, battle: BattleAPI?) {
    }

    override fun reportFleetDespawnedToListener(fleet: CampaignFleetAPI, reason: FleetDespawnReason?, param: Any?) {
        if (this.isFunctional()) {
            if (reason == FleetDespawnReason.REACHED_DESTINATION) {
                val route = RouteManager.getInstance().getRoute(this.getRouteSourceId(), fleet)
                if (route.getCustom() is MilitaryBase.PatrolFleetData) {
                    val custom = route.getCustom() as MilitaryBase.PatrolFleetData
                    if (custom.spawnFP > 0) {
                        val fraction = (fleet.getFleetPoints() / custom.spawnFP).toFloat()
                        this.returningPatrolValue += fraction
                    }
                }
            }
        }
    }

    override fun spawnFleet(route: RouteData): CampaignFleetAPI? {
        val custom = route.getCustom() as MilitaryBase.PatrolFleetData
        val type = custom.type
        val random = route.getRandom()
        var combat = 0.0f
        var tanker = 0.0f
        var freighter = 0.0f
        val fleetType = type.getFleetType()
        when (type) {
            PatrolType.FAST -> combat = Math.round(3.0f + random.nextFloat() * 2.0f).toFloat() * 5.0f
            PatrolType.COMBAT -> {
                combat = Math.round(6.0f + random.nextFloat() * 3.0f).toFloat() * 5.0f
                tanker = Math.round(random.nextFloat()).toFloat() * 5.0f
            }

            PatrolType.HEAVY -> {
                combat = Math.round(10.0f + random.nextFloat() * 5.0f).toFloat() * 5.0f
                tanker = Math.round(random.nextFloat()).toFloat() * 10.0f
                freighter = Math.round(random.nextFloat()).toFloat() * 10.0f
            }
        }

        val params = FleetParamsV3(
            this.market,
            null as Vector2f?,
            LocalFactionStrings.MEDUSAS_HEAD,
            route.getQualityOverride(),
            fleetType,
            combat,
            freighter,
            tanker,
            0.0f,
            0.0f,
            0.0f,
            0.0f
        )
        params.timestamp = route.timestamp
        params.random = random
        params.modeOverride = Misc.getShipPickMode(this.market)
        params.modeOverride = ShipPickMode.PRIORITY_THEN_ALL
        val fleet = FleetFactoryV3.createFleet(params)
        if (fleet != null && !fleet.isEmpty()) {
            fleet.setFaction(this.market.factionId, true)
            fleet.setNoFactionInName(true)
            fleet.addEventListener(this)
            fleet.getMemoryWithoutUpdate().set("\$isPatrol", true)
            fleet.getMemoryWithoutUpdate().set("\$cfai_ignoreOtherFleets", true, 0.3f)
            if (type == PatrolType.FAST || type == PatrolType.COMBAT) {
                fleet.getMemoryWithoutUpdate().set("\$isCustomsInspector", true)
            }

            val postId = Ranks.POST_PATROL_COMMANDER
            val rankId = when (type) {
                PatrolType.FAST -> Ranks.SPACE_LIEUTENANT
                PatrolType.COMBAT -> Ranks.SPACE_COMMANDER
                PatrolType.HEAVY -> Ranks.SPACE_CAPTAIN
                else -> Ranks.SPACE_CAPTAIN
            }

            fleet.getCommander().setPostId(postId)
            fleet.getCommander().setRankId(rankId)

            for (member in fleet.getFleetData().getMembersListCopy()) {
                if (member.isCapital()) {
                    member.setVariant(member.getVariant().clone(), false, false)
                    member.getVariant().setSource(VariantSource.REFIT)
                    member.getVariant().addTag("no_autofit")
                    member.getVariant().addTag("consistent_weapon_drops")
                }
            }

            this.market.getContainingLocation().addEntity(fleet)
            fleet.setFacing(Math.random().toFloat() * 360.0f)
            fleet.setLocation(
                this.market.getPrimaryEntity().getLocation().x,
                this.market.getPrimaryEntity().getLocation().y
            )
            fleet.addScript(PatrolAssignmentAIV4(fleet, route))
            if (custom.spawnFP <= 0) {
                custom.spawnFP = fleet.getFleetPoints()
            }

            return fleet
        } else {
            return null
        }
    }

    fun getRouteSourceId(): String {
        return this.getMarket().getId() + "_" + "lionsguard"
    }

    override fun isAvailableToBuild(): Boolean {
        return false
    }

    override fun showWhenUnavailable(): Boolean {
        return false
    }

    override fun canImprove(): Boolean {
        return false
    }

    override fun adjustCommodityDangerLevel(commodityId: String?, level: RaidDangerLevel): RaidDangerLevel? {
        return level.next()
    }

    override fun adjustItemDangerLevel(itemId: String?, data: String?, level: RaidDangerLevel): RaidDangerLevel? {
        return level.next()
    }
}