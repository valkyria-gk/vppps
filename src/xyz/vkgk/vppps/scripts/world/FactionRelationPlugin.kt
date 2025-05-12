package xyz.vkgk.vppps.scripts.world

import com.fs.starfarer.api.campaign.RepLevel
import com.fs.starfarer.api.campaign.SectorAPI
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin
import com.fs.starfarer.api.impl.campaign.ids.Factions
import xyz.vkgk.vppps.LocalFactionStrings
import xyz.vkgk.vppps.SupportedModFactionStrings

class FactionRelationPlugin : SectorGeneratorPlugin {
    override fun generate(sectorAPI: SectorAPI?) {
        val perseanDomain = sectorAPI!!.getFaction(LocalFactionStrings.PERSEAN_FREE_DOMAIN)

        // Jane Starsector
        perseanDomain.setRelationship(Factions.PLAYER, RepLevel.NEUTRAL)
        // Vanilla
        perseanDomain.setRelationship(Factions.HEGEMONY, -1.00f)
        perseanDomain.setRelationship(Factions.PERSEAN, -0.65f)
        perseanDomain.setRelationship(Factions.TRITACHYON, RepLevel.INHOSPITABLE)
        perseanDomain.setRelationship(Factions.LUDDIC_CHURCH, RepLevel.INHOSPITABLE)
        perseanDomain.setRelationship(Factions.KOL, RepLevel.HOSTILE)
        perseanDomain.setRelationship(Factions.LUDDIC_PATH, RepLevel.HOSTILE)
        perseanDomain.setRelationship(Factions.INDEPENDENT, RepLevel.FAVORABLE)
        perseanDomain.setRelationship(Factions.PIRATES, RepLevel.INHOSPITABLE)
        perseanDomain.setRelationship(Factions.DIKTAT, RepLevel.SUSPICIOUS)
        // Spoiler stuff
        perseanDomain.setRelationship(Factions.REMNANTS, RepLevel.SUSPICIOUS)
        perseanDomain.setRelationship(Factions.DERELICT, RepLevel.NEUTRAL)
        perseanDomain.setRelationship(Factions.THREAT, RepLevel.HOSTILE)
        perseanDomain.setRelationship(Factions.DWELLER, RepLevel.HOSTILE)

        // Explicitly supported 3rd party stuff
        perseanDomain.setRelationship(SupportedModFactionStrings.MAYASURA, RepLevel.WELCOMING)
        perseanDomain.setRelationship(SupportedModFactionStrings.VAYRA_RESEARCHERS, RepLevel.WELCOMING)
        perseanDomain.setRelationship(SupportedModFactionStrings.VAYRA_REVOLUTIONARIES, RepLevel.WELCOMING)
        perseanDomain.setRelationship(SupportedModFactionStrings.VAYRA_MERCHANTS, RepLevel.SUSPICIOUS)
        perseanDomain.setRelationship(SupportedModFactionStrings.VAYRA_ASHEN_KEEPERS, RepLevel.SUSPICIOUS)
        perseanDomain.setRelationship(SupportedModFactionStrings.HMI_FANG, RepLevel.HOSTILE)
        perseanDomain.setRelationship(SupportedModFactionStrings.HMI_DRACO, RepLevel.HOSTILE)
        perseanDomain.setRelationship(SupportedModFactionStrings.HAZARD_MINING, RepLevel.INHOSPITABLE)
        perseanDomain.setRelationship(SupportedModFactionStrings.LEGION_INFERNALIS, RepLevel.INHOSPITABLE)
        perseanDomain.setRelationship(SupportedModFactionStrings.LEGION_DAEMONS, RepLevel.HOSTILE)
        perseanDomain.setRelationship(SupportedModFactionStrings.KASSADAR, RepLevel.HOSTILE)
    }
}