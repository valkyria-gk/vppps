package xyz.vkgk.vppps.campaign.submarkets

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUIAPI
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.RepLevel
import com.fs.starfarer.api.impl.campaign.ids.HullMods
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin
import com.fs.starfarer.api.util.Highlights
import com.fs.starfarer.api.util.Misc
import xyz.vkgk.vppps.LocalFactionStrings
import java.awt.Color

class MedusasHeadMarketPlugin : BaseSubmarketPlugin() {

    override fun getTooltipAppendix(ui: CoreUIAPI?): String? {
        if (market.factionId != LocalFactionStrings.PERSEAN_FREE_DOMAIN) {
            return "Defunct due to hostile occupation"
        }
        if (!Global.getSector().playerFleet.isTransponderOn) {
            return "Requires: Transponder on"
        }
        if (Misc.getCommissionFactionId() != LocalFactionStrings.PERSEAN_FREE_DOMAIN) {
            return "Requires: ${market.faction.displayName} commmission"
        }
        if (!market.faction.relToPlayer.isAtWorst(RepLevel.COOPERATIVE)) {
            return "Requires: ${market.faction.displayName} - ${RepLevel.COOPERATIVE.displayName}"
        }

        return super.getTooltipAppendix(ui)
    }

    override fun getTooltipAppendixHighlights(ui: CoreUIAPI): Highlights? {
        val appendix = this.getTooltipAppendix(ui)
        if (appendix == null) {
            return null
        } else {
            val h = Highlights()
            h.setText(*arrayOf<String>(appendix))
            h.setColors(*arrayOf<Color?>(Misc.getNegativeHighlightColor()))
            return h
        }
    }

    override fun isEnabled(ui: CoreUIAPI?): Boolean {
        if (market.factionId != LocalFactionStrings.PERSEAN_FREE_DOMAIN) {
            return false
        }
        if (!Global.getSector().playerFleet.isTransponderOn) {
            return false
        }
        if (Misc.getCommissionFactionId() != LocalFactionStrings.PERSEAN_FREE_DOMAIN) {
            return false
        }
        return market.faction.relToPlayer.isAtWorst(RepLevel.COOPERATIVE)
    }

    override fun updateCargoPrePlayerInteraction() {
        this.sinceLastCargoUpdate = 0f
        if (okToUpdateShipsAndWeapons()) {
            this.sinceSWUpdate = 0f

            pruneShips(0f)
            pruneWeapons(0f)

            val doctrineOverride = submarket.faction.doctrine.clone()
            doctrineOverride.shipQuality = 5

            val factionId = submarket.faction.id
            addShips(
                factionId, 300f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, FactionAPI.ShipPickMode.PRIORITY_THEN_ALL, doctrineOverride
            )

            addWeapons(
                10, 20, 5, factionId
            )

            for (member in cargo.mothballedShips.membersListCopy) {
                member.variant.hullMods.remove(HullMods.ILL_ADVISED)
            }
        }
        cargo.sort()
    }

    override fun isParticipatesInEconomy(): Boolean {
        return false
    }
}