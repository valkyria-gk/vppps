package xyz.vkgk.vppps.campaign.fleets

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.fleets.SDFBase
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.missions.FleetCreatorMission
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers
import com.fs.starfarer.api.impl.campaign.missions.hub.MissionFleetAutoDespawn
import xyz.vkgk.vppps.LocalFactionStrings
import xyz.vkgk.vppps.LocalMarketStrings


class DefenceFleetFreeDomain : SDFBase() {
    override fun getFactionId(): String? {
        return LocalFactionStrings.PERSEAN_FREE_DOMAIN
    }

    override fun getSourceMarket(): MarketAPI? {
        return Global.getSector().economy.getMarket(LocalMarketStrings.SEKHMET)
    }

    override fun getDefeatTriggerToUse(): String? {
        return "FreeDomainLilithDefenseFleetDefeated"
    }

    override fun spawnFleet(): CampaignFleetAPI? {
        val source = sourceMarket!!

        val mission = FleetCreatorMission(random)
        mission.beginFleet()

        val location = source.locationInHyperspace

        mission.triggerCreateFleet(
            HubMissionWithTriggers.FleetSize.MAXIMUM,
            HubMissionWithTriggers.FleetQuality.SMOD_1,
            LocalFactionStrings.MEDUSAS_HEAD,
            FleetTypes.PATROL_LARGE,
            location
        )
        mission.triggerSetFleetSizeFraction(1.25f)
        mission.triggerSetFleetDoctrineComp(
            5,
            2,
            0,
        )
        mission.triggerSetFleetDoctrineOther(
            4,
            4
        )
        mission.triggerSetFleetDoctrineQuality(3, 3, 2)

        mission.triggerSetPatrol()
        mission.triggerSetFleetMemoryValue(MemFlags.MEMORY_KEY_SOURCE_MARKET, source)
        mission.triggerPatrolAllowTransponderOff()
        mission.triggerOrderFleetPatrol(source.starSystem)
        mission.triggerFleetSetName("Vanguard")


        val fleet = mission.createFleet()
        fleet.setFaction(factionId)
        fleet.removeScriptsOfClass(MissionFleetAutoDespawn::class.java)
        source.containingLocation.addEntity(fleet)

        val sourceLocation = source.planetEntity.location
        fleet.setLocation(sourceLocation.x, sourceLocation.y)
        fleet.setFacing(random.nextFloat() * 360f)

        return fleet
    }
}