package xyz.vkgk.vppps.hullmods

import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.impl.campaign.ids.Stats

class PointDefenseIntegration : BaseHullMod() {
    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        stats!!.dynamic.getMod(Stats.SMALL_PD_MOD).modifyMult(id, 0.5f)
        stats.dynamic.getMod(Stats.MEDIUM_PD_MOD).modifyMult(id, 0.5f)
        stats.dynamic.getMod(Stats.LARGE_PD_MOD).modifyMult(id, 0.5f)
    }

    override fun affectsOPCosts(): Boolean {
        return true
    }
}