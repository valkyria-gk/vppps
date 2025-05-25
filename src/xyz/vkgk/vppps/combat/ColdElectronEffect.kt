package xyz.vkgk.vppps.combat

import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.listeners.AdvanceableListener
import com.fs.starfarer.api.util.TimeoutTracker

class ColdElectronEffect : BeamEffectPlugin {
    protected var wasZero: Boolean = true

    override fun advance(amount: Float, engine: CombatEngineAPI?, beam: BeamAPI) {
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