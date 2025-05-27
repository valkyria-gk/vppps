package xyz.vkgk.vppps.combat

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BeamAPI
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.EmpArcEntityAPI
import com.fs.starfarer.api.combat.EmpArcEntityAPI.EmpArcParams
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.util.vector.Vector2f
import kotlin.math.max
import kotlin.math.min

object VisualEffects {
    fun spawnEmpArcEffect(
        engine: CombatEngineAPI,
        beam: BeamAPI,
    ) {
        val from: Vector2f? = beam.from
        val to: Vector2f? = beam.rayEndPrevFrame
        val ship: ShipAPI? = beam.source

        val params = EmpArcParams()
        params.segmentLengthMult = 4f

        params.glowSizeMult = 0.5f
        params.brightSpotFadeFraction = 0.33f
        params.brightSpotFullFraction = 0.5f
        params.movementDurMax = 0.2f
        params.flickerRateMult = 0.5f

        val dist = Misc.getDistance(from, to)
        val minBright = 100f
        if (dist * params.brightSpotFullFraction < minBright) {
            params.brightSpotFullFraction = minBright / max(minBright, dist)
        }

        val thickness = 20f

        val arc = engine.spawnEmpArcVisual(
            from,
            ship,
            to,
            ship,
            thickness,  // thickness
            beam.fringeColor,
            beam.coreColor,
            params
        )

        arc.setSingleFlickerMode(true)
        arc.setUpdateFromOffsetEveryFrame(true)
    }

    fun spawnAbyssalLightningEffect(
        engine: CombatEngineAPI,
        beam: BeamAPI,
        arcSpeed: Float = 10000.0f
    ) {
        val from = beam.from
        val to = beam.rayEndPrevFrame
        val dist: Float = Misc.getDistance(from, to)
        val params = EmpArcParams()

        params.segmentLengthMult = 8.0f
        params.zigZagReductionFactor = 0.15f
        params.fadeOutDist = 50.0f
        params.minFadeOutMult = 10.0f
        params.flickerRateMult = 0.3f
        val fraction = min(0.33f, 300.0f / dist)
        params.brightSpotFullFraction = fraction
        params.brightSpotFadeFraction = fraction
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