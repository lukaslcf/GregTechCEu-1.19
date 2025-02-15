package com.gregtechceu.gtceu.api.data.worldgen.generator;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreFeatureConfiguration;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreFeatureEntry;
import com.gregtechceu.gtceu.api.data.worldgen.WorldGeneratorUtils;
import com.gregtechceu.gtceu.common.data.GTFeatures;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.*;
import java.util.function.Function;

public abstract class VeinGenerator {
    public static final Codec<Codec<? extends VeinGenerator>> REGISTRY_CODEC = ResourceLocation.CODEC
            .flatXmap(rl -> Optional.ofNullable(WorldGeneratorUtils.VEIN_GENERATORS.get(rl))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error("No VeinGenerator with id " + rl + " registered")),
                    obj -> Optional.ofNullable(WorldGeneratorUtils.VEIN_GENERATORS.inverse().get(obj))
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error("VeinGenerator " + obj + " not registered")));
    public static final Codec<VeinGenerator> DIRECT_CODEC = REGISTRY_CODEC.dispatchStable(VeinGenerator::codec, Function.identity());

    protected GTOreFeatureEntry entry;

    public VeinGenerator() {
    }

    public VeinGenerator(GTOreFeatureEntry entry) {
        this.entry = entry;
    }

    public ConfiguredFeature<?, ?> createConfiguredFeature() {
        build();
        GTOreFeatureConfiguration config = new GTOreFeatureConfiguration(entry);
        return new ConfiguredFeature<>(GTFeatures.ORE, config);
    }

    /**
     * @return Map of [block|material, chance]
     */
    public abstract Map<Either<BlockState, Material>, Integer> getAllEntries();

    public List<BlockState> getAllBlocks() {
        return getAllEntries().keySet().stream().map(either -> either.map(Function.identity(), material -> ChemicalHelper.getBlock(TagPrefix.ore, material).defaultBlockState())).toList();
    }

    public List<Material> getAllMaterials() {
        return getAllEntries().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .map(either -> either.map(state -> ChemicalHelper.getMaterial(state.getBlock()) != null ? ChemicalHelper.getMaterial(state.getBlock()).material() : null, Function.identity())).filter(Objects::nonNull)
                .toList();
    }

    public List<Integer> getAllChances() {
        return getAllEntries().values().stream().toList();
    }

    public List<Map.Entry<Integer, Material>> getValidMaterialsChances() {
        return getAllEntries().entrySet().stream()
                .filter(entry -> entry.getKey().map(state -> ChemicalHelper.getMaterial(state.getBlock()) != null ? ChemicalHelper.getMaterial(state.getBlock()).material() : null, Function.identity()) != null)
                .map(entry -> Map.entry(entry.getValue(), entry.getKey().map(state -> ChemicalHelper.getMaterial(state.getBlock()) != null ? ChemicalHelper.getMaterial(state.getBlock()).material() : null, Function.identity())))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @HideFromJS
    public abstract boolean generate(WorldGenLevel level, RandomSource random, GTOreFeatureEntry entry, BlockPos origin);

    @HideFromJS
    public abstract VeinGenerator build();

    @HideFromJS
    public GTOreFeatureEntry parent() {
        return entry;
    }

    public abstract Codec<? extends VeinGenerator> codec();
}
