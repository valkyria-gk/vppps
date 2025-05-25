package xyz.vkgk.vppps.combat

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import org.lwjgl.util.vector.Vector2f
import kotlin.math.max
import kotlin.math.min

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

                    handleShortCircuitEffect(engine, beam)
                }
            }
        }
    }

    fun handleShortCircuitEffect(
        engine: CombatEngineAPI,
        beam: BeamAPI
    ) {
        val from = beam.getFrom()
        val to = beam.getRayEndPrevFrame()
        val dist: Float = Misc.getDistance(from, to)

        if (dist > 100.0f && (Math.random().toFloat() > 0.5f)) {
            val params = EmpArcParams()
            params.segmentLengthMult = 8.0f
            params.zigZagReductionFactor = 0.15f
            params.fadeOutDist = 50.0f
            params.minFadeOutMult = 10.0f
            params.flickerRateMult = 0.3f
            val fraction = min(0.33f, 300.0f / dist)
            params.brightSpotFullFraction = fraction
            params.brightSpotFadeFraction = fraction
            val arcSpeed = GAMMA_RAY_SHORT_CIRCUIT_ARC_SPEED
            params.movementDurOverride = max(0.05f, dist / arcSpeed)
            val ship: ShipAPI? = beam.getSource()
            val arc: EmpArcEntityAPI =
                engine.spawnEmpArcVisual(
                    from,
                    ship,
                    to,
                    ship,
                    80.0f,
                    beam.fringeColor,
                    beam.coreColor,
                    params
                )
            arc.setCoreWidthOverride(40.0f)
            arc.setRenderGlowAtStart(false)
            arc.setFadedOutAtStart(true)
            arc.setSingleFlickerMode(true)
            val pt = Vector2f.add(from, to, Vector2f())
            pt.scale(0.5f)
            Global.getSoundPlayer().playSound("abyssal_glare_lightning", 1.0f, 1.0f, pt, Vector2f())
        }
    }

    companion object {
        const val GAMMA_RAY_SHORT_CIRCUIT_ARC_SPEED: Float = 20000.0f
    }
}
