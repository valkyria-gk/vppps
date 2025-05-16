package xyz.vkgk.vppps.hullmods

import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize

const val COST_REDUCTION = 10.0f

class LargeMissileIntegration : BaseHullMod() {
    override fun applyEffectsBeforeShipCreation(hullSize: HullSize?, stats: MutableShipStatsAPI, id: String?) {
        stats.dynamic.getMod("large_missile_mod").modifyFlat(id, -COST_REDUCTION)
    }

    override fun getDescriptionParam(index: Int, hullSize: HullSize?): String? {
        return if (index == 0) COST_REDUCTION.toInt().toString() else null
    }

    override fun affectsOPCosts(): Boolean {
        return true
    }
}