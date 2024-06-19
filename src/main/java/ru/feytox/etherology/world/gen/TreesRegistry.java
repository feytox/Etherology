package ru.feytox.etherology.world.gen;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.mixin.FoliagePlacerTypeAccessor;
import ru.feytox.etherology.mixin.TrunkPlacerTypeAccessor;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.world.trees.PeachFoliagePlacer;
import ru.feytox.etherology.world.trees.PeachTrunkPlacer;

public class TreesRegistry {

    public static final TrunkPlacerType<PeachTrunkPlacer> PEACH_TRUNK_PLACER =
            TrunkPlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_trunk_placer", PeachTrunkPlacer.CODEC);

    public static final FoliagePlacerType<PeachFoliagePlacer> PEACH_FOLIAGE_PLACER =
            FoliagePlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_foliage_placer", PeachFoliagePlacer.CODEC);

    public static TreeFeatureConfig.Builder peach() {
        return new TreeFeatureConfig.Builder(
                BlockStateProvider.of(DecoBlocks.PEACH_LOG),
                new PeachTrunkPlacer(11, 5, 3),
                BlockStateProvider.of(DecoBlocks.PEACH_LEAVES),
                new PeachFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1)),
                new TwoLayersFeatureSize(2, 0, 2)
        );
    }

    public static void registerTrees() {

    }
}
