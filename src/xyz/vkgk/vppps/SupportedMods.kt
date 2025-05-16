package xyz.vkgk.vppps

import com.fs.starfarer.api.Global

enum class SupportedMods(val identifier: String) {
    BIGBEANS_SHIP_COMPILATION("BSC"),
    TAHLAN_SHIPWORKS("tahlan"),
    SHIP_WEAPONS_PACK("swp"),
    VAYRAS_MERGED("vayramerged");

    val isEnabled : Boolean
        get() = Global.getSettings().modManager.isModEnabled(this.identifier)
}