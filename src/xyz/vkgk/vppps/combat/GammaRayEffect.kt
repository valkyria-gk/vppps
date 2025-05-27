package xyz.vkgk.vppps.combat

import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.util.IntervalUtil

class GammaRayEffect : BeamEffectPlugin {
    private val fireInterval = IntervalUtil(0.5f, 0.75f)

    override fun advance(amount: Float, engine: CombatEngineAPI, beam: BeamAPI) {
        val target = beam.getDamageTarget()
        if (target is ShipAPI && beam.getBrightness() >= 1.0f) {

            this.fireInterval.advance(amount)
            if (this.fireInterval.intervalElapsed()) {
                val hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getRayEndPrevFrame())

                var pierceChance = target.fluxLevel + 0.1f
                pierceChance *= target.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT)

                val piercedShield = hitShield && Math.random().toFloat() < pierceChance
                val shouldEmpArc = !hitShield || piercedShield
                if (shouldEmpArc) {
                    val point = beam.getRayEndPrevFrame()
                    val emp = beam.getDamage().getFluxComponent() * 1.0f
                    val dam = beam.getDamage().getDamage() * 1.0f
                    engine.spawnEmpArcPierceShields(
                        beam.getSource(),
                        point,
                        beam.getDamageTarget(),
                        beam.getDamageTarget(),
                        DamageType.ENERGY,
                        dam,
                        emp,
                        100000.0f,
                        "tachyon_lance_emp_impact",
                        beam.getWidth() + 9.0f,
                        beam.getFringeColor(),
                        beam.getCoreColor()
                    )

                    VisualEffects.spawnEmpArcEffect(engine, beam)
                    VisualEffects.spawnAbyssalLightningEffect(engine, beam)
                }
            }
        }
    }
}
