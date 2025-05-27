package xyz.vkgk.vppps.combat

import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.TimeoutTracker

class ColdElectronEffect : BeamEffectPlugin {
    private val fireInterval = IntervalUtil(0.25f, 1.75f)
    protected var wasZero: Boolean = true

    fun advanceEmpComponent(amount: Float, engine: CombatEngineAPI, beam: BeamAPI) {
        val target = beam.getDamageTarget()
        if (target is ShipAPI && beam.getBrightness() >= 1.0f) {
            var dur = beam.getDamage().getDpsDuration()
            if (!this.wasZero) {
                dur = 0.0f
            }

            this.wasZero = beam.getDamage().getDpsDuration() <= 0.0f
            this.fireInterval.advance(dur)
            if (this.fireInterval.intervalElapsed()) {
                val ship = target
                val hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getRayEndPrevFrame())
                var pierceChance = target.fluxLevel - 0.1f
                pierceChance *= ship.getMutableStats().getDynamic().getValue("shield_pierced_mult")

                val piercedShield = hitShield && Math.random().toFloat() < pierceChance
                if (!hitShield || piercedShield) {
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
                }
            }
        }
    }

    fun advanceShieldSuppressionComponent(amount: Float, engine: CombatEngineAPI, beam: BeamAPI) {
        val target = beam.getDamageTarget()
        if (target is ShipAPI && beam.getBrightness() >= 1.0f && beam.getWeapon() != null) {
            var dur = beam.getDamage().getDpsDuration()
            if (!this.wasZero) {
                dur = 0.0f
            }

            this.wasZero = beam.getDamage().getDpsDuration() <= 0.0f
            if (dur > 0.0f) {
                val hitShield = target.getShield() != null && target.getShield().isWithinArc(beam.getTo())
                if (hitShield) {
                    val ship = target
                    if (!ship.hasListenerOfClass(ColdElectronDamageTaken::class.java)) {
                        ship.addListener(ColdElectronDamageTaken(ship))
                    }

                    val listeners =
                        ship.getListeners<ColdElectronDamageTaken?>(ColdElectronDamageTaken::class.java)
                    if (listeners.isEmpty()) {
                        return
                    }

                    val listener = listeners.get(0) as ColdElectronDamageTaken
                    listener.notifyHit(beam.getWeapon())
                }
            }
        }
    }

    override fun advance(
        p0: Float,
        p1: CombatEngineAPI,
        p2: BeamAPI
    ) {
        advanceEmpComponent(p0, p1, p2)
        advanceShieldSuppressionComponent(p0, p1, p2)
    }

    class ColdElectronDamageTaken(protected var ship: ShipAPI) : AdvanceableListener {
        protected var recentHits: TimeoutTracker<WeaponAPI?> = TimeoutTracker<WeaponAPI?>()

        fun notifyHit(w: WeaponAPI?) {
            this.recentHits.add(w, EFFECT_DURATION, EFFECT_DURATION)
        }

        override fun advance(amount: Float) {
            this.recentHits.advance(amount)
            val beams = this.recentHits.getItems().size
            if (beams > 0) {
                val bonus = beams * DAMAGE_BONUS_PER_BEAM
                this.ship.getMutableStats().getShieldDamageTakenMult().modifyMult(DAMAGE_MOD_ID, 1.0f + bonus * 0.01f)
            } else {
                this.ship.removeListener(this)
                this.ship.getMutableStats().getShieldDamageTakenMult().unmodify(DAMAGE_MOD_ID)
            }
        }

        companion object {
            var EFFECT_DURATION: Float = 1.0f
            var DAMAGE_BONUS_PER_BEAM: Float = 15.0f
            var DAMAGE_MOD_ID: String = "cold_electron_damage_modifier"
        }
    }
}