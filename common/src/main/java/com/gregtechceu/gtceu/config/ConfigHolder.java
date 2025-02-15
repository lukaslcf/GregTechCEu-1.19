package com.gregtechceu.gtceu.config;

import com.gregtechceu.gtceu.GTCEu;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

/**
 * @author KilaBash
 * @date 2023/2/14
 * @implNote ConfigHolder
 */
@Config(id = GTCEu.MOD_ID)
public class ConfigHolder {
    public static ConfigHolder INSTANCE;

    public static void init() {
        INSTANCE = Configuration.registerConfig(ConfigHolder.class, ConfigFormats.yaml()).getConfigInstance();
    }

    @Configurable
    public RecipeConfigs recipes = new RecipeConfigs();
    @Configurable
    public WorldGenConfigs worldgen = new WorldGenConfigs();
    @Configurable
    public MachineConfigs machines = new MachineConfigs();
    @Configurable
    public ClientConfigs client = new ClientConfigs();
    @Configurable
    @Configurable.Comment("Config options for Mod Compatibility")
    public CompatibilityConfigs compat = new CompatibilityConfigs();

    public static class RecipeConfigs {
        @Configurable
        @Configurable.Comment({"Whether to generate Flawed and Chipped Gems for materials and recipes involving them.",
                "Useful for mods like TerraFirmaCraft.", "Default: false"})
        public boolean generateLowQualityGems = true; // default true
        @Configurable
        @Configurable.Comment({"Whether to remove Block/Ingot compression and decompression in the Crafting Table.", "Default: false"})
        public boolean disableManualCompression = true; // default true
        @Configurable
        @Configurable.Comment({"Change the recipe of Rods in the Lathe to 1 Rod and 2 Small Piles of Dust, instead of 2 Rods.", "Default: false"})
        public boolean harderRods = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to make crafting recipes for Bricks, Firebricks, and Coke Bricks harder.", "Default: false"})
        public boolean harderBrickRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to nerf Wood crafting to 2 Planks from 1 Log, and 2 Sticks from 2 Planks.", "Default: false"})
        public boolean nerfWoodCrafting = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to make Wood related recipes harder.", "Excludes sticks and planks.", "Default: false"})
        public boolean hardWoodRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Recipes for Buckets, Cauldrons, Hoppers, and Iron Bars" +
                " require Iron Plates, Rods, and more.", "Default: true"})
        public boolean hardIronRecipes = true; // default true
        @Configurable
        @Configurable.Comment({"Whether to make Redstone related recipes harder.", "Default: false"})
        public boolean hardRedstoneRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to make Vanilla Tools and Armor recipes harder.", "Excludes Flint and Steel, and Buckets.", "Default: false"})
        public boolean hardToolArmorRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to make miscellaneous recipes harder.", "Default: false"})
        public boolean hardMiscRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to make Glass related recipes harder. Default: true"})
        public boolean hardGlassRecipes = true; // default true
        @Configurable
        @Configurable.Comment({"Whether to nerf the Paper crafting recipe.", "Default: true"})
        public boolean nerfPaperCrafting = true; // default true
        @Configurable
        @Configurable.Comment({"Recipes for items like Iron Doors, Trapdoors, Anvil" +
                " require Iron Plates, Rods, and more.", "Default: false"})
        public boolean hardAdvancedIronRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to make coloring blocks like Concrete or Glass harder.", "Default: false"})
        public boolean hardDyeRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to remove charcoal smelting recipes from the vanilla furnace.", "Default: true"})
        public boolean harderCharcoalRecipe = true; // default true
        @Configurable
        @Configurable.Comment({"Whether to make the Flint and Steel recipe require steel parts.", "Default: true."})
        public boolean flintAndSteelRequireSteel = true; // default true
        @Configurable
        @Configurable.Comment({"Whether to remove Vanilla Block Recipes from the Crafting Table.", "Default: false"})
        public boolean removeVanillaBlockRecipes = false; // default false
        @Configurable
        @Configurable.Comment({"Whether to remove Vanilla TNT Recipe from the Crafting Table.", "Default: true"})
        public boolean removeVanillaTNTRecipe = true; // default true
    }

    public static class CompatibilityConfigs {

        @Configurable
        @Configurable.Comment("Config options regarding GTEU compatibility with other energy systems")
        public EnergyCompatConfig energy = new EnergyCompatConfig();

        @Configurable
        @Configurable.Comment({"Whether to hide facades of all blocks in JEI and creative search menu.", "Default: true"})
        public boolean hideFacadesInJEI = true;

        @Configurable
        @Configurable.Comment({"Whether to hide filled cells in JEI and creative search menu.", "Default: true"})
        public boolean hideFilledCellsInJEI = true;

        @Configurable
        @Configurable.Comment({"Whether Gregtech should remove smelting recipes from the vanilla furnace for ingots requiring the Electric Blast Furnace.", "Default: true"})
        public boolean removeSmeltingForEBFMetals = true;

        public static class EnergyCompatConfig {

            @Configurable
            @Configurable.Comment({"Enable Native GTEU to Platform native Energy (RF and alike) on GT Cables and Wires.", "This does not enable nor disable Converters.", "Default: true"})
            public boolean nativeEUToPlatformNative = true;

            @Configurable
            @Configurable.Comment({"Enable GTEU to Platform native (and vice versa) Converters.", "Default: false"})
            public boolean enablePlatformConverters = false;

            @Configurable
            @Configurable.Comment({"Platform native Energy to GTEU ratio for converting FE to EU.", "Only affects converters.", "Default: 4 FE/Energy == 1 EU"})
            @Configurable.Range(min = 1, max = 16)
            public int platformToEuRatio = 4;

            @Configurable
            @Configurable.Comment({"GTEU to Platform native Energy ratio for converting EU to FE.", "Affects native conversion and Converters.", "Default: 4 FE/Energy == 1 EU"})
            @Configurable.Range(min = 1, max = 16)
            public int euToPlatformRatio = 4;
        }
    }

    public static class WorldGenConfigs {
        @Configurable
        @Configurable.Comment({"Should all Stone Types drop unique Ore Item Blocks?", "Default: false (meaning only Stone, Netherrack, and Endstone)"})
        public boolean allUniqueStoneTypes;

        @Configurable
        @Configurable.Comment({"Should Sand-like ores fall?", "This includes gravel, sand, and red sand ores.", "Default: false (no falling ores)"})
        public boolean sandOresFall;

        @Configurable
        @Configurable.Range(min = 0, max = 127)
        @Configurable.Comment({"Radius that ore veins will check for existing ones.", "If one is found, the vein will not spawn.", "Default: 3"})
        public int oreVeinScanRadius = 3;

        @Configurable
        @Configurable.Range(min = 0, max = 8)
        @Configurable.Comment({"Maximum worldgen feature size in chunks",
                "if a vein is larger than this, Minecraft complains in logs.",
                "If that happens, either increase this or make your veins smaller.",
                "NOTE: Larger veins can cause noticeable worldgen lag!",
                "Default: 2"})
        public int maxFeatureChunkSize = 2;

        @Configurable
        @Configurable.Comment({"Debug ore vein placement? (will print placed veins to server's debug.log)", "Default: false (no placement printout in debug.log)"})
        public boolean debugWorldgen;

        @Configurable
        @Configurable.Comment({"Rubber Tree spawn chance (% per chunk)", "Default: 0.5"})
        public float rubberTreeSpawnChance = 0.5f;

        @Configurable
        @Configurable.Comment({"Prevents regular vanilla ores from being generated outside GregTech ore veins", "Default: true"})
        public boolean removeVanillaOreGen = true;

        @Configurable
        @Configurable.Comment({"Prevents vanilla's large ore veins from being generated", "Default: true"})
        public boolean removeVanillaLargeOreVeins = true;
    }

    public static class MachineConfigs {
        @Configurable
        @Configurable.Comment({"Whether insufficient energy supply should reset Machine recipe progress to zero.",
                "If true, progress will reset.", "If false, progress will decrease to zero with 2x speed", "Default: false"})
        public boolean recipeProgressLowEnergy = false;

        @Configurable
        @Configurable.Comment({"Whether to require a Wrench, Wirecutter, or other GregTech tools to break machines, casings, wires, and more.", "Default: false"})
        public boolean requireGTToolsForBlocks;
        @Configurable
        @Configurable.Comment({"Whether machines explode in rainy weather or when placed next to certain terrain, such as fire or lava", "Default: false"})
        public boolean doTerrainExplosion;
        @Configurable
        @Configurable.Comment({"Whether machines or boilers damage the terrain when they explode.",
                "Note machines and boilers always explode when overloaded with power or met with special conditions, regardless of this config.", "Default: true"})
        public boolean doesExplosionDamagesTerrain;
        @Configurable
        @Configurable.Comment({"Divisor for Recipe Duration per Overclock.", "Default: 2.0"})
        @Configurable.DecimalRange(min = 2.0, max = 3.0)
        @Configurable.Gui.NumberFormat("0.0#")
        public double overclockDivisor = 2.0;
        @Configurable
        @Configurable.Comment({"Whether to play machine sounds while machines are active.", "Default: true"})
        public boolean machineSounds = true;
        @Configurable
        @Configurable.Comment({"Whether Steam Multiblocks should use Steel instead of Bronze.", "Default: false"})
        public boolean steelSteamMultiblocks;
        @Configurable
        @Configurable.Comment({"Whether to enable the cleanroom, required for various recipes.", "Default: true"})
        public boolean enableCleanroom = true;
        @Configurable
        @Configurable.Comment({"Whether multiblocks should ignore all cleanroom requirements.", "This does nothing if enableCleanroom is false.", "Default: false"})
        public boolean cleanMultiblocks = false;
        @Configurable
        @Configurable.Comment({"Block to replace mined ores with in the miner and multiblock miner.", "Default: minecraft:cobblestone"})
        public String replaceMinedBlocksWith = "minecraft:cobblestone";
        @Configurable
        @Configurable.Comment({"Whether to enable the Maintenance Hatch, required for Multiblocks.", "Default: true"})
        public boolean enableMaintenance = true;
        @Configurable
        @Configurable.Comment({"Whether the machine's circuit slot need to be inserted a real circuit."})
        public boolean ghostCircuit = true;
        @Configurable
        @Configurable.Comment({"Wether to add a \"Bedrock Ore Miner\" (also enables bedrock ore generation)", "Default: false"})
        public boolean doBedrockOres = false;
        @Configurable
        @Configurable.Comment({"What Kind of material should the bedrock ore miner output?", "Default: \"raw\""})
        public String bedrockOreDropTagPrefix = "raw";
        @Configurable
        @Configurable.Comment({"Wether to add a \"Processing Array\"", "Default: true"})
        public boolean doProcessingArray = true;
    }

    public static class ClientConfigs {
        @Configurable
        @Configurable.Comment({"Whether or not to enable Emissive Textures for GregTech Machines.", "Default: true"})
        public boolean machinesEmissiveTextures = true;
        @Configurable
        @Configurable.Comment({"The default color to overlay onto machines.", "16777215 (0xFFFFFF in decimal) is no coloring (default).",
                "13819135 (0xD2DCFF in decimal) is the classic blue from GT5."})
        @Configurable.Range(min = 0, max = 0xFFFFFF)
        @Configurable.Gui.ColorValue
        public int defaultPaintingColor = 0xFFFFFF;
    }

}
