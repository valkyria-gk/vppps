package xyz.vkgk.vppps

import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.api.campaign.SectorAPI

class BlueprintInitializerPlugin : BlueprintSyncPlugin {
    override fun getHullBlueprints(settingsAPI: SettingsAPI): Set<String> {
        val extraHullsToAdd = HashSet<String>()

        if (SupportedMods.VAYRAS_MERGED.isEnabled) {
            extraHullsToAdd.addAll(
                arrayOf(
                    "vayra_tyrant",
                    "vayra_tyrant_lp",
                    "vayra_heavy_drone_tender",
                    "vayra_badger_p",
                    "vayra_subjugator",
                    "vayra_subjugator_lp",
                    "vayra_hatchetman_lp",
                    // "vayra_persecutor",
                    "vayra_persecutor_lp",
                    // "vayra_camel",
                    "vayra_camel_p",
                    // "vayra_falchion",
                    "vayra_falchion_p",
                    "vayra_bruiser",
                    "vayra_bruiser_lp",
                    //"vayra_direwolf",
                    "vayra_typhon",
                    "vayra_huntress",
                    "vayra_oppressor",
                )
            )
        }
        if (SupportedMods.TAHLAN_SHIPWORKS.isEnabled) {
            extraHullsToAdd.addAll(
                arrayOf(
                    "tahlan_Hresvelgr",
                    "tahlan_skyrend",
                    "tahlan_Flagellator",
                    "tahlan_Castigator",
                    //"tahlan_Kodai",
                    "tahlan_nelson",
                    //"tahlan_darnus", // this ship is a huge mistake
                    "tahlan_Eagle_P"
                )
            )
        }
        if (SupportedMods.SHIP_WEAPONS_PACK.isEnabled) {
            extraHullsToAdd.addAll(
                arrayOf(
                    "swp_vindicator",
                    "swp_vindicator_o",
                    "swp_beholder",
                    "swp_punisher",
                    "swp_zenith",
                    "swp_albatross",
                    "swp_archer",
                    "swp_vulture",
                    "swp_vulture_p",
                    "swp_caliber",
                    "swp_liberator",
                    "swp_wolf_luddic_path",
                    //"swp_buffalo_luddic_path",
                )
            )
        }
        if (SupportedMods.BIGBEANS_SHIP_COMPILATION.isEnabled) {
            extraHullsToAdd.addAll(
                arrayOf(
                    "BSC_Hammerhead_LP",
                    "BSC_Mudskipper_MK3",
                    "BSC_Preybird",
                    "BSC_Preybird_LP",
                    "BSC_Hammerhead_MK2",
                    "BSC_Hartfell",
                    "BSC_Hartfell_P",
                    "BSC_Ruswall_P",
                    "BSC_Carnyx",
                    "BSC_Chrysaor",
                    //"BSC_Timberwolf",
                    "BSC_Accipiter",
                    "BSC_Buzzard",
                    "BSC_Cassina",
                    "BSC_Baldric",
                    "BSC_Constable",
                    "BSC_Terringzean",
                    "BSC_Threave",
                )
            )
        }

        return extraHullsToAdd
    }

    override fun initializeBlueprints(settingsAPI: SettingsAPI) {
        val freeDomainSpec = settingsAPI.getFactionSpec(LocalFactionStrings.PERSEAN_FREE_DOMAIN)
        val medusasHeadSpec = settingsAPI.getFactionSpec(LocalFactionStrings.MEDUSAS_HEAD)

        val hulls = getHullBlueprints(settingsAPI)
        hulls.forEach { s ->
            assert(settingsAPI.getHullSpec(s) != null, { "Couldn't find ${s}, did you spell the Hull ID correctly?" })
        }

        freeDomainSpec.shipsWhenImporting.addAll(hulls)
        medusasHeadSpec.shipsWhenImporting.addAll(hulls)

        freeDomainSpec.knownShips.addAll(freeDomainSpec.shipsWhenImporting)
        medusasHeadSpec.knownShips.addAll(medusasHeadSpec.shipsWhenImporting)
    }

    override fun syncBlueprintsInGame(sectorAPI: SectorAPI, settingsAPI: SettingsAPI) {
        val perseanFreeDomainFaction = sectorAPI.getFaction(LocalFactionStrings.PERSEAN_FREE_DOMAIN)
        val medusasHeadFaction = sectorAPI.getFaction(LocalFactionStrings.MEDUSAS_HEAD)


        for (hullId in getHullBlueprints(settingsAPI)) {
            perseanFreeDomainFaction.addUseWhenImportingShip(settingsAPI.getHullSpec(hullId)!!.hullId)
        }

        perseanFreeDomainFaction.alwaysKnownShips.addAll(perseanFreeDomainFaction.knownShips)
        medusasHeadFaction.alwaysKnownShips.addAll(perseanFreeDomainFaction.alwaysKnownShips)
    }
}