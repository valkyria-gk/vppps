package xyz.vkgk.vppps.scripts.world.systems

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.impl.campaign.ids.Planets
import com.fs.starfarer.api.impl.campaign.ids.StarTypes
import com.fs.starfarer.api.impl.campaign.ids.Terrain
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain
import xyz.vkgk.vppps.scripts.world.LagrangePoint
import xyz.vkgk.vppps.scripts.world.OrbitHelper
import java.awt.Color
import xyz.vkgk.vppps.scripts.world.StarSystemGeneratorPlugin

class Oleg : StarSystemGeneratorPlugin {
    override fun createSystem(
        sectorAPI: SectorAPI, hyperspace: LocationAPI
    ): StarSystemAPI {
        val system = sectorAPI.createStarSystem("Oleg")

        val olegStar = system.initStar(
            "oleg",
            StarTypes.ORANGE,
            460f,
            460f,
            10f,
            1f,
            3f,
        )
        system.lightColor = Color.ORANGE

        val chelyabinsk = system.addPlanet(
            "chelyabinsk", olegStar, "Chelyabinsk", "toxic", 90f, 120f, 720f, OrbitHelper.calculateOrbitPeriod(720f)
        )

        system.addRingBand(olegStar, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 900f, 100f);
        system.addRingBand(olegStar, "misc", "rings_asteroids0", 256f, 1, Color.white, 256f, 1000f, 90f)
        val belt = system.addTerrain(
            Terrain.RING, BaseRingTerrain.RingParams(
                100 + 256f, 950f, null, "The Little Belt"
            )
        )
        belt.setCircularOrbit(olegStar, 0f, 0f, 90f);

        val wum = system.addPlanet(
            "wum", olegStar, "Wum", Planets.ARID, 120f, 120f, 1250f, OrbitHelper.calculateOrbitPeriod(1250f)
        )

        val tunna = system.addPlanet(
            "tunna",
            olegStar,
            "Tunna",
            Planets.PLANET_TERRAN_ECCENTRIC,
            220f,
            100f,
            1600f,
            OrbitHelper.calculateOrbitPeriod(1600f)
        )

        val hink = system.addPlanet(
            "hink", olegStar, "Hink", Planets.BARREN_BOMBARDED, 0f, 35f, 320f, OrbitHelper.calculateOrbitPeriod(320f)
        )

        val quincy = system.addPlanet(
            "quincy",
            olegStar,
            "Quincy",
            Planets.TUNDRA,
            175f,
            130f,
            2200f,
            OrbitHelper.calculateOrbitPeriod(2200f),
        )

        val norfolk = system.addPlanet(
            "norfolk",
            quincy,
            "Norfolk",
            Planets.BARREN_VENUSLIKE,
            230f,
            70f,
            200f,
            OrbitHelper.calculateOrbitPeriod(200f),
        )

        val pablo = system.addPlanet(
            "pablo",
            olegStar,
            "Pablo",
            Planets.GAS_GIANT,
            20f,
            220f,
            5200f,
            OrbitHelper.calculateOrbitPeriod(5200f),
        )

        val dalton = system.addCustomEntity(
            "dalton_station", "Dalton Industrial Orbital", "station_mining00", Factions.PIRATES
        )

        OrbitHelper.placeStableOrbit(
            pablo, dalton, LagrangePoint.L1, true
        )

        system.addAsteroidBelt(pablo, 100, 400f, 256f, 150f, 250f, Terrain.ASTEROID_BELT, null);
        system.addAsteroidBelt(pablo, 100, 500f, 256f, 150f, 250f, Terrain.ASTEROID_BELT, null);

        system.addRingBand(pablo, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 350f, 80f);
        system.addRingBand(pablo, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 550f, 100f);

        system.addRingBand(pablo, "misc", "rings_asteroids0", 256f, 1, Color.white, 256f, 400f, 90f)
        system.addRingBand(pablo, "misc", "rings_asteroids0", 256f, 3, Color.white, 256f, 500f, 90f)

        val pabloBelt = system.addTerrain(
            Terrain.RING, BaseRingTerrain.RingParams(
                100 + 256f, 450f, null, "Pablo's Skirt"
            )
        )

        return system
    }
}