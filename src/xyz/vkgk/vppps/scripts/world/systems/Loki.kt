package xyz.vkgk.vppps.scripts.world.systems

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.ids.Planets
import com.fs.starfarer.api.impl.campaign.ids.StarTypes
import xyz.vkgk.vppps.scripts.world.OrbitHelper
import xyz.vkgk.vppps.scripts.world.StarSystemGeneratorPlugin

class Loki : StarSystemGeneratorPlugin {
    override fun createSystem(
        sectorAPI: SectorAPI,
        hyperspace: LocationAPI
    ): StarSystemAPI {
        var system = sectorAPI.createStarSystem("Loki")

        var lokiStar = system.initStar(
            "loki",
            StarTypes.ORANGE,
            300f,
            300f,
            10f,
            1f,
            3f,
        )

        val jesmond = system.addPlanet(
            "jesmond",
            lokiStar,
            "Jesmond",
            Planets.BARREN_VENUSLIKE,
            260f,
            100f,
            1400f,
            OrbitHelper.calculateOrbitPeriod(1400f)
        )

        val desort = system.addPlanet(
            "desort",
            lokiStar,
            "Desort",
            Planets.GAS_GIANT,
            145f,
            320f,
            3200f,
            OrbitHelper.calculateOrbitPeriod(3200f)
        )

        val odense = system.addPlanet(
            "odense",
            desort,
            "Odense",
            Planets.TUNDRA,
            0f,
            100f,
            800f,
            OrbitHelper.calculateOrbitPeriod(800f)
        )

        val krion  = system.addPlanet(
            "krion",
            lokiStar,
            "Krion",
            Planets.CRYOVOLCANIC,
            0f,
            90f,
            5400f,
            OrbitHelper.calculateOrbitPeriod(5400f)
        )

        return system
    }
}