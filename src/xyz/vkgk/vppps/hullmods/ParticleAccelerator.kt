package xyz.vkgk.vppps.hullmods

import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.BeamAPI
import com.fs.starfarer.api.combat.CombatEntityAPI
import com.fs.starfarer.api.combat.DamageAPI
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc

const val FLUX_COST_MODIFIER: Float = -15.0f
const val DAMAGE_BONUS_MODIFIER: Float = 15.0f

class ParticleAccelerator : BaseHullMod() {
    override fun shouldAddDescriptionToTooltip(
        hullSize: ShipAPI.HullSize?,
        ship: ShipAPI?,
        isForModSpec: Boolean
    ): Boolean {
        return false
    }

    override fun addPostDescriptionSection(
        tooltip: TooltipMakerAPI?,
        hullSize: ShipAPI.HullSize?,
        ship: ShipAPI?,
        width: Float,
        isForModSpec: Boolean
    ) {
        val padding = 10.0f
        val highlightColour = Misc.getHighlightColor()
        tooltip?.addPara(
            "Beam weapons deal %s more damage and always deal %s damage to shields.",
            padding,
            highlightColour,
            "${DAMAGE_BONUS_MODIFIER.toInt()}%",
            "hard flux"
        )
        tooltip?.addPara(
            "Reduces the flux cost by %s.",
            padding,
            highlightColour,
            "${FLUX_COST_MODIFIER.toInt()}%"
        )
    }

    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        stats?.beamWeaponDamageMult?.modifyPercent(id, DAMAGE_BONUS_MODIFIER)
        stats?.beamWeaponFluxCostMult?.modifyPercent(id, FLUX_COST_MODIFIER)
    }

    override fun applyEffectsAfterShipCreation(ship: ShipAPI, id: String) {
        ship.addListener(ParticleAcceleratorDamageDealtModifier(ship))
    }

    class ParticleAcceleratorDamageDealtModifier : DamageDealtModifier {
        val owner: ShipAPI

        constructor(ship: ShipAPI) {
            this.owner = ship
        }

        override fun modifyDamageDealt(
            param: Any?,
            target: CombatEntityAPI?,
            damage: DamageAPI?,
            point: org.lwjgl.util.vector.Vector2f,
            shieldHit: Boolean
        ): String? {
            if ((param is DamagingProjectileAPI) && param is BeamAPI) {
                damage?.isForceHardFlux = true
            }

            return null
        }
    }
}
