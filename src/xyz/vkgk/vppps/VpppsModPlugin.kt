package xyz.vkgk.vppps

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.impl.campaign.fleets.PersonalFleetScript
import com.fs.starfarer.api.impl.campaign.fleets.SDFBase
import com.fs.starfarer.api.impl.campaign.shared.SharedData
import xyz.vkgk.vppps.campaign.fleets.DefenceFleetFreeDomain
import xyz.vkgk.vppps.scripts.world.BanditNebulaPlugin
import xyz.vkgk.vppps.scripts.world.FactionRelationPlugin

class VpppsModPlugin : BaseModPlugin() {
    companion object {
        var enableFactionPerseanFreeDomain = true;
    }

    override fun onApplicationLoad() {
        BlueprintInitializerPlugin().initializeBlueprints(Global.getSettings())
    }

    override fun onGameLoad(newGame: Boolean) {
        val sectorAPI = Global.getSector()
        val settingsAPI = Global.getSettings()

        BlueprintInitializerPlugin().syncBlueprintsInGame(sectorAPI, settingsAPI)

        ensureFleetScripts(sectorAPI)
    }

    override fun onNewGameAfterEconomyLoad() {
        val sectorAPI = Global.getSector()

        ensureFleetScripts(sectorAPI)
    }

    override fun onNewGame() {
        val sectorApi = Global.getSector()

        if (enableFactionPerseanFreeDomain) {
            BanditNebulaPlugin().generate(sectorApi)
            FactionRelationPlugin().generate(sectorApi)
            SharedData.getData().personBountyEventData.addParticipatingFaction(LocalFactionStrings.PERSEAN_FREE_DOMAIN)
        }
    }

    fun ensureFleetScripts(sectorAPI: SectorAPI) {
        val fleetScripts: Map<Class<out PersonalFleetScript>, () -> PersonalFleetScript> = mapOf(
            DefenceFleetFreeDomain::class.java to { DefenceFleetFreeDomain() }
        )

        for (fleetScript in fleetScripts) {
            if (!sectorAPI.hasScript(fleetScript.key))
                sectorAPI.addScript(fleetScript.value.invoke())
        }
    }
}