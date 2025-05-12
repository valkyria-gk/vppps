package xyz.vkgk.vppps.scripts.world.systems

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.impl.campaign.ids.Terrain
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams
import xyz.vkgk.vppps.LocalFactionStrings
import xyz.vkgk.vppps.scripts.world.LagrangePoint
import xyz.vkgk.vppps.scripts.world.OrbitHelper
import xyz.vkgk.vppps.scripts.world.StarSystemGeneratorPlugin
import java.awt.Color


class Lilith : StarSystemGeneratorPlugin {
    override fun createSystem(sectorAPI: SectorAPI, hyperspace: LocationAPI): StarSystemAPI {
        val system = sectorAPI.createStarSystem("Lilith")

        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg")

        val lilithStar = system.initStar(
            "lilith",
            "star_yellow",
            600f,
            600f,
            10f,
            1f,
            3f,
        )
        system.lightColor = Color(255, 245, 185)

        val planetAbelard = system.addPlanet(
            "abelard",
            lilithStar,
            "Abelard",
            "barren-desert",
            20f,
            100f,
            1600f,
            OrbitHelper.calculateOrbitPeriod(1600f),
        )

        val buoy = system.addCustomEntity(
            "lilith_buoy",
            "Lilith Buoy",
            "nav_buoy",
            LocalFactionStrings.PERSEAN_FREE_DOMAIN,
        )
        OrbitHelper.placeStableOrbit(planetAbelard, buoy, LagrangePoint.L3, pointingDown = true)

        system.addAsteroidBelt(lilithStar, 100, 2400f, 256f, 150f, 250f, Terrain.ASTEROID_BELT, null);
        system.addAsteroidBelt(lilithStar, 100, 2700f, 256f, 150f, 250f, Terrain.ASTEROID_BELT, null);
        system.addAsteroidBelt(lilithStar, 100, 3000f, 128f, 200f, 300f, Terrain.ASTEROID_BELT, null);
        system.addAsteroidBelt(lilithStar, 100, 3300f, 188f, 200f, 300f, Terrain.ASTEROID_BELT, null);
        system.addAsteroidBelt(lilithStar, 100, 3700f, 256f, 200f, 300f, Terrain.ASTEROID_BELT, null);

        system.addRingBand(lilithStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 2400f, 100f);
        system.addRingBand(lilithStar, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 2500f, 70f);
        system.addRingBand(lilithStar, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 2600f, 90f);
        system.addRingBand(lilithStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 3150f, 80f);
        system.addRingBand(lilithStar, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 3400f, 90f);
        system.addRingBand(lilithStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, 3500f, 100f);

        system.addRingBand(lilithStar, "misc", "rings_asteroids0", 256f, 1, Color.white, 256f, 2700f, 90f)
        system.addRingBand(lilithStar, "misc", "rings_asteroids0", 256f, 3, Color.white, 256f, 3000f, 90f)
        system.addRingBand(lilithStar, "misc", "rings_asteroids0", 256f, 2, Color.white, 256f, 3300f, 90f)
        val sandpaper = system.addTerrain(
            Terrain.RING,
            BaseRingTerrain.RingParams(
                1200f + 256,
                3000f,
                null,
                "The Sandpaper"
            )
        )
        sandpaper.setCircularOrbit(lilithStar, 0f, 0f, 90f);

        val planetSekhmet = system.addPlanet(
            "sekhmet",
            lilithStar,
            "Sekhmet",
            "toxic",
            135f,
            120f,
            5600f,
            OrbitHelper.calculateOrbitPeriod(5600f),
        )

        val sekhmetMagneticField = system.addTerrain(
            Terrain.MAGNETIC_FIELD,
            MagneticFieldParams(
                200f,  // terrain effect band width
                200f,  // terrain effect middle radius
                planetSekhmet,  // entity that it's around
                120f,  // visual band start
                320f,  // visual band end
                Color(50, 30, 100, 30),  // base color
                1f,  // probability to spawn aurora sequence, checked once/day when no aurora in progress
                Color(90, 180, 40),
                    Color(130, 145, 90),
                Color(165, 110, 145),
                Color(95, 55, 160),
                Color(45, 0, 130),
                Color(20, 0, 130),
                Color(10, 0, 150)
            )
        )
        sekhmetMagneticField.setCircularOrbit(planetSekhmet, 0f, 0f, 120f)

        val sekhmetStation: SectorEntityToken =
            system.addCustomEntity(
                "lilith_citadel",
                "Citadel Maahes",
                "station_side02",
                LocalFactionStrings.PERSEAN_FREE_DOMAIN,
            )
        sekhmetStation.setInteractionImage("illustrations", "orbital")
        sekhmetStation.setCircularOrbitPointingDown(planetSekhmet, 0f, 320f, 60f)

        var planetBastet = system.addPlanet(
            "bastet",
            planetSekhmet,
            "Bastet",
            "barren-desert",
            235f,
            50f,
            520f,
            120f,
        )

        val planetBeowulf = system.addPlanet(
            "beowulf",
            lilithStar,
            "Beowulf",
            "gas_giant",
            230f,
            320f,
            9000f,
            OrbitHelper.calculateOrbitPeriod(9000f),
        )

        val beowulfSpec = planetBeowulf.spec
        beowulfSpec.planetColor = Color(50, 100, 255, 255)
        beowulfSpec.atmosphereColor = Color(110, 120, 130, 150)
        beowulfSpec.cloudColor = Color(195, 230, 255, 200)
        beowulfSpec.iconColor = Color(100, 120, 130, 255)
        beowulfSpec.glowTexture = Global.getSettings().getSpriteName("hab_glows", "aurorae")
        beowulfSpec.glowColor = Color(235, 38, 8, 145)
        beowulfSpec.isUseReverseLightForGlow = true
        beowulfSpec.atmosphereThickness = 0.5f
        beowulfSpec.tilt = 210f
        planetBeowulf.applySpecChanges()

        val beowulfMagneticField = system.addTerrain(
            Terrain.MAGNETIC_FIELD,
            MagneticFieldParams(
                200f,  // terrain effect band width
                400f,  // terrain effect middle radius
                planetBeowulf,  // entity that it's around
                300f,  // visual band start
                500f,  // visual band end
                Color(50, 30, 100, 30),  // base color
                1f,  // probability to spawn aurora sequence, checked once/day when no aurora in progress
                Color(50, 20, 110, 130),
                Color(150, 30, 120, 150),
                Color(200, 50, 130, 190),
                Color(250, 70, 150, 240),
                Color(200, 80, 130, 255),
                Color(75, 0, 160),
                Color(127, 0, 255)
            )
        )
        beowulfMagneticField.setCircularOrbit(planetBeowulf, 0f, 0f, 120f)

        system.addRingBand(planetBeowulf, "misc", "rings_asteroids0", 256f, 2, Color.white, 256f, 2100f, 90f)
        system.addRingBand(planetBeowulf, "misc", "rings_asteroids0", 256f, 3, Color.white, 256f, 650f, 100f)
        system.addRingBand(planetBeowulf, "misc", "rings_ice0", 256f, 1, Color.white, 256f, 700f, 90f, Terrain.RING, "Beowulf's Tears")

        var planetStrozywei = system.addPlanet(
            "strozywei",
            planetBeowulf,
            "Strozywei",
            "tundra",
            0f,
            120f,
            1200f,
            60f,
        )
        planetStrozywei.spec.tilt = 20f

        var planetLem = system.addPlanet(
            "lem",
            planetBeowulf,
            "Lem",
            "water",
            180f,
            90f,
            1600f,
            60f,
        )
        val lemStation: SectorEntityToken =
            system.addCustomEntity(
                "lem_station",
                "Solaris Orbital",
                "station_side02",
                LocalFactionStrings.PERSEAN_FREE_DOMAIN,
            )
        lemStation.setInteractionImage("illustrations", "orbital")
        OrbitHelper.placeStableOrbit(planetLem, lemStation, LagrangePoint.L1, pointingDown = true)

        // Azazel trojans
        val beowulfL4 = system.addTerrain(
            Terrain.ASTEROID_FIELD,
            AsteroidFieldParams(
                500f,
                700f,
                20,
                30,
                4f,
                16f,
                "Beowulf L4 Asteroids"
            )
        )

        val beowulfL5 = system.addTerrain(
            Terrain.ASTEROID_FIELD,
            AsteroidFieldParams(
                500f,
                700f,
                20,
                30,
                4f,
                16f,
                "Beowulf L5 Asteroids"
            )
        )

        OrbitHelper.placeStableOrbit(planetBeowulf, beowulfL4, LagrangePoint.L4)
        OrbitHelper.placeStableOrbit(planetBeowulf, beowulfL5, LagrangePoint.L5)

        val relay = system.addCustomEntity(
            "lilith_relay",
            "Lilith Relay",
            "comm_relay",
            LocalFactionStrings.PERSEAN_FREE_DOMAIN,
        )
        OrbitHelper.placeStableOrbit(planetBeowulf, relay, LagrangePoint.L4, pointingDown = true)

        // Beowulf loc
        val ring: SectorEntityToken =
            system.addCustomEntity(
                "lilith_gate",
                "Lilith Gate",
                "inactive_gate",
                Factions.NEUTRAL,
            )
        OrbitHelper.placeStableOrbit(planetBeowulf, ring, LagrangePoint.L3, pointingDown = true)

        val limboBasePeriod = 240f
        system.addRingBand(lilithStar, "misc", "rings_asteroids0", 256f, 3, Color.white, 256f, 11800f, limboBasePeriod)
        system.addRingBand(lilithStar, "misc", "rings_ice0", 256f, 0, Color.white, 256f, 11900f, limboBasePeriod - 10f)
        system.addRingBand(lilithStar, "misc", "rings_ice0", 256f, 1, Color.white, 256f, 12000f, limboBasePeriod)
        system.addRingBand(lilithStar, "misc", "rings_ice0", 256f, 3, Color.white, 256f, 12100f, limboBasePeriod + 10f)
        system.addRingBand(lilithStar, "misc", "rings_ice0", 256f, 0, Color.white, 256f, 12200f, limboBasePeriod - 10f)

        val limboRing = system.addTerrain(
            Terrain.RING,
            BaseRingTerrain.RingParams(
                400f + 256,
                12000f,
                null,
                "Limbo Ring"
            )
        )
        limboRing.setCircularOrbit(lilithStar, 0f, 0f, limboBasePeriod);


        var planetAzazel = system.addPlanet(
            "azazel",
            lilithStar,
            "Azazel",
            "ice_giant",
            45f,
            280f,
            13500f,
            OrbitHelper.calculateOrbitPeriod(13000f),
        )
        planetAzazel.spec.tilt = 185f

        var planetJanMayen = system.addPlanet(
            "jan_mayen",
            planetAzazel,
            "Jan Mayen",
            "cryovolcanic",
            0f,
            100f,
            880f,
            40f,
        )

        // Azazel trojans
        val azazelL4 = system.addTerrain(
            Terrain.ASTEROID_FIELD,
            AsteroidFieldParams(
                500f,
                700f,
                20,
                30,
                4f,
                16f,
                "Azazel L4 Asteroids"
            )
        )

        val azazelL5 = system.addTerrain(
            Terrain.ASTEROID_FIELD,
            AsteroidFieldParams(
                500f,
                700f,
                20,
                30,
                4f,
                16f,
                "Azazel L5 Asteroids"
            )
        )

        OrbitHelper.placeStableOrbit(planetAzazel, azazelL4, LagrangePoint.L4)
        OrbitHelper.placeStableOrbit(planetAzazel, azazelL5, LagrangePoint.L5)

        val outer_jump = Global.getFactory().createJumpPoint(
            "azazel_jump",
            "Azazel L5 Jump-point"
        )
        system.addEntity(outer_jump)
        OrbitHelper.placeStableOrbit(planetAzazel, outer_jump, LagrangePoint.L5)

        // Azazel loc
        val sensors: SectorEntityToken =
            system.addCustomEntity(
                "lilith_sensors",
                "Lilith Sensors Array",
                "sensor_array",
                LocalFactionStrings.PERSEAN_FREE_DOMAIN,
            )
        OrbitHelper.placeStableOrbit(planetAzazel, sensors, LagrangePoint.L3, pointingDown = true)

        var planetPlato = system.addPlanet(
            "plato",
            lilithStar,
            "Plato",
            "barren",
            0f,
            50f,
            18000f,
            OrbitHelper.calculateOrbitPeriod(18000f),
        )

        system.autogenerateHyperspaceJumpPoints(true, true)
        return system
    }
}