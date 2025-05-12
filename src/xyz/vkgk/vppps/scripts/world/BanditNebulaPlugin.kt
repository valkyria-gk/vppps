package xyz.vkgk.vppps.scripts.world

import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.procgen.Constellation
import com.fs.starfarer.api.impl.campaign.procgen.NameGenData
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin
import com.fs.starfarer.api.util.Misc
import xyz.vkgk.vppps.scripts.world.systems.Lilith

class BanditNebulaPlugin : SectorGeneratorPlugin {
    override fun generate(sectorAPI: SectorAPI) {
        val hyperspace = Global.getSector().hyperspace
        val systems = arrayOf(
            Lilith().createSystem(sectorAPI, hyperspace)
        )

        val banditConstellation = Constellation(
            Constellation.ConstellationType.NEBULA,
            StarAge.AVERAGE,
        )
        val nameGeneratorData = NameGenData("null", "null")
        val constellationName = ProcgenUsedNames.NamePick(nameGeneratorData, "Bandit", "null")
        banditConstellation.namePick = constellationName

        val hyperspaceTerrainPlugin = Misc.getHyperspaceTerrain().plugin as HyperspaceTerrainPlugin
        val nebulaEditor = NebulaEditor(hyperspaceTerrainPlugin)

        for (system in systems) {
            banditConstellation.systems.add(system)
            system.constellation = banditConstellation
            StarSystemGenerator.addSystemwideNebula(system, StarAge.AVERAGE)

            nebulaEditor.clearArc(system.location.x, system.location.y, 0f, system.maxRadiusInHyperspace, 0f, 360f)
            nebulaEditor.clearArc(system.location.x, system.location.y, 0f, system.maxRadiusInHyperspace * 1.5f, 0f, 360f, 0.25f)
        }
    }
}