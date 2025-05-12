package xyz.vkgk.vppps

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.shared.SharedData
import xyz.vkgk.vppps.scripts.world.BanditNebulaPlugin
import xyz.vkgk.vppps.scripts.world.FactionRelationPlugin

class VpppsModPlugin : BaseModPlugin() {
    companion object {
        var enableFactionPerseanFreeDomain = true;
    }

    override fun onNewGame() {
        val sectorApi = Global.getSector()

        if (enableFactionPerseanFreeDomain) {
            BanditNebulaPlugin().generate(sectorApi)
            FactionRelationPlugin().generate(sectorApi)
            SharedData.getData().personBountyEventData.addParticipatingFaction(LocalFactionStrings.PERSEAN_FREE_DOMAIN)
        }
    }
}