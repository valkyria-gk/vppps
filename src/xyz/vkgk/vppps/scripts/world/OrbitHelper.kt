package xyz.vkgk.vppps.scripts.world

import com.fs.starfarer.api.campaign.SectorEntityToken

enum class LagrangePoint(val angleOffset: Float, val orbitRadiusOffset: Float, val focusRadiusAddFactor: Float) {
    L1(0f, -300f, 1.0f),
    L2(0f, +300f, 1.0f),
    L3(+180f, 0f, 0.0f),
    L4(-60f, 0f, 0.0f),
    L5(+60f, 0f, 0.0f),
}

object OrbitHelper {
    fun calculateOrbitPeriod(radius: Float) : Float {
        return 700f/8000f * radius
    }

    fun placeStableOrbit(origo: SectorEntityToken, body: SectorEntityToken, lagrangePoint: LagrangePoint, pointingDown: Boolean = false) {
        val angle = origo.circularOrbitAngle + lagrangePoint.angleOffset
        val radius = origo.circularOrbitRadius + (lagrangePoint.focusRadiusAddFactor * origo.radius) + lagrangePoint.orbitRadiusOffset
        val focus = origo.orbit.focus

        if (pointingDown) {
            body.setCircularOrbitPointingDown(focus, angle, radius, origo.circularOrbitPeriod)
        } else {
            body.setCircularOrbit(focus, angle, radius, origo.circularOrbitPeriod)
        }
    }
}