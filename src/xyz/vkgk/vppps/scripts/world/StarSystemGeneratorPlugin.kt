package xyz.vkgk.vppps.scripts.world

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.StarSystemAPI

interface StarSystemGeneratorPlugin {
    fun createSystem(sectorAPI: SectorAPI, hyperspace: LocationAPI) : StarSystemAPI
}