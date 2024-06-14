package ru.feytox.etherology.world.gen;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.mixin.TrunkPlacerTypeAccessor;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.world.trees.PeachTrunkPlacer;

import java.util.OptionalInt;

public class EtherTreesGeneration {

    public static final TrunkPlacerType<PeachTrunkPlacer> PEACH_TRUNK_PLACER =
            TrunkPlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_trunk_placer", PeachTrunkPlacer.CODEC);

    public static TreeFeatureConfig.Builder peach() {
        return new TreeFeatureConfig.Builder(
                BlockStateProvider.of(DecoBlocks.PEACH_LOG),
                new PeachTrunkPlacer(10, 2, 6),
                BlockStateProvider.of(DecoBlocks.PEACH_LEAVES),
                new AcaciaFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
                new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
        );
    }

    public static void registerTrees() {
//        BiomeModifications.addFeature(
//                // TODO: 29/04/2023 specify biome
//                BiomeSelectors.foundInOverworld(),
//                GenerationStep.Feature.VEGETAL_DECORATION,
//                EPlacedFeatures.PEACH_PLACED_KEY
//        );
    }
}
