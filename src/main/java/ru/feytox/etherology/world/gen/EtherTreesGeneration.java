package ru.feytox.etherology.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.UpwardsBranchingTrunkPlacer;
import ru.feytox.etherology.DecoBlocks;
import ru.feytox.etherology.world.EPlacedFeatures;

public class EtherTreesGeneration {
    public static void generateTrees() {
        BiomeModifications.addFeature(
                // TODO: 29/04/2023 specify biome
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EPlacedFeatures.PEACH_PLACED_KEY
        );
    }

    public static TreeFeatureConfig.Builder peach() {
        return new TreeFeatureConfig.Builder(
                BlockStateProvider.of(DecoBlocks.PEACH_LOG),
                new UpwardsBranchingTrunkPlacer(4, 3, 2, ConstantIntProvider.create(4), 0.4f, ConstantIntProvider.create(3), RegistryEntryList.of()),
                BlockStateProvider.of(DecoBlocks.PEACH_LEAVES),
                new BushFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
                new TwoLayersFeatureSize(0, 0, 0)
        );
    }
}
