package xyz.vkgk.vppps

import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.api.campaign.SectorAPI

interface BlueprintSyncPlugin {
    fun getHullBlueprints(settingsAPI: SettingsAPI) : Set<String>

    fun initializeBlueprints(settingsAPI: SettingsAPI)
    fun syncBlueprintsInGame(sectorAPI: SectorAPI, settingsAPI: SettingsAPI)
}