package xyz.vkgk.vppps.scripts.world.systems

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.PlanetSearchData
import com.fs.starfarer.api.impl.campaign.ids.Conditions
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator
import xyz.vkgk.vppps.scripts.world.OrbitHelper
import xyz.vkgk.vppps.scripts.world.StarSystemGeneratorPlugin

class LostPlanetHolder : StarSystemGeneratorPlugin {
    override fun createSystem(
        sectorAPI: SectorAPI,
        hyperspace: LocationAPI
    ): StarSystemAPI {
        val system = sectorAPI.createStarSystem("Unknown Location")
        system.setName("Unknown Location")
        system.setType(StarSystemGenerator.StarSystemType.DEEP_SPACE)
        system.addTag("theme_unsafe")
        system.addTag("theme_hidden")
        system.addTag("theme_special")
        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        val center = system.initNonStarCenter()
        val thePlanet = system.addPlanet(
            "the_planet_that_once_was",
            center,
            "The World That Once Was",
            "cryovolcanic",
            0f,
            120f,
            1000f,
            OrbitHelper.calculateOrbitPeriod(1000f)
        )
        thePlanet.market.addCondition(Conditions.THIN_ATMOSPHERE)
        thePlanet.market.addCondition(Conditions.DARK)
        thePlanet.market.addCondition(Conditions.VERY_COLD)
        thePlanet.market.addCondition(Conditions.ORGANICS_COMMON)
        thePlanet.market.addCondition(Conditions.ORE_SPARSE)
        thePlanet.market.addCondition(Conditions.RARE_ORE_SPARSE)
        thePlanet.market.addCondition(Conditions.VOLATILES_TRACE)
        thePlanet.market.addCondition(Conditions.RUINS_VAST)

        return system
    }
}