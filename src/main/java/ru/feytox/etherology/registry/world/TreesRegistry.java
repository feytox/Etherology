package ru.feytox.etherology.registry.world;

import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.mixin.FoliagePlacerTypeAccessor;
import ru.feytox.etherology.mixin.TreeDecoratorTypeAccessor;
import ru.feytox.etherology.mixin.TrunkPlacerTypeAccessor;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.world.trees.PeachFoliagePlacer;
import ru.feytox.etherology.world.trees.PeachLanternDecorator;
import ru.feytox.etherology.world.trees.PeachTrunkPlacer;
import ru.feytox.etherology.world.trees.PeachWeepingDecorator;

import java.util.Collections;

@UtilityClass
public class TreesRegistry {

    public static final TrunkPlacerType<PeachTrunkPlacer> PEACH_TRUNK_PLACER =
            TrunkPlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_trunk_placer", PeachTrunkPlacer.CODEC);

    public static final FoliagePlacerType<PeachFoliagePlacer> PEACH_FOLIAGE_PLACER =
            FoliagePlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_foliage_placer", PeachFoliagePlacer.CODEC);

    public static final TreeDecoratorType<PeachWeepingDecorator> PEACH_WEEPING_DECORATOR =
            TreeDecoratorTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_weeping_decorator", PeachWeepingDecorator.CODEC);

    public static final TreeDecoratorType<PeachLanternDecorator> PEACH_LANTERN_DECORATOR =
            TreeDecoratorTypeAccessor.callRegister(Etherology.MOD_ID + ":peach_lantern_decorator", PeachLanternDecorator.CODEC);

    public static final TreeFeatureConfig PEACH_CONFIG = new TreeFeatureConfig.Builder(
            BlockStateProvider.of(DecoBlocks.PEACH_LOG),
            new PeachTrunkPlacer(11, 5, 3),
            BlockStateProvider.of(DecoBlocks.PEACH_LEAVES),
            new PeachFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1)),
            new TwoLayersFeatureSize(2, 0, 2))
            .decorators(ImmutableList.of(PeachWeepingDecorator.INSTANCE, PeachLanternDecorator.INSTANCE))
            .build();

    public static final TreeFeatureConfig PEACH_SAPLING_CONFIG = new TreeFeatureConfig.Builder(
            BlockStateProvider.of(DecoBlocks.PEACH_LOG),
            new PeachTrunkPlacer(11, 5, 3),
            BlockStateProvider.of(DecoBlocks.PEACH_LEAVES),
            new PeachFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1)),
            new TwoLayersFeatureSize(2, 0, 2))
            .decorators(Collections.singletonList(PeachWeepingDecorator.INSTANCE))
            .build();

    public static void registerTrees() {}
}
