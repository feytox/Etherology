package ru.feytox.etherology.registry.world;

import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.mixin.FoliagePlacerTypeAccessor;
import ru.feytox.etherology.mixin.TreeDecoratorTypeAccessor;
import ru.feytox.etherology.mixin.TrunkPlacerTypeAccessor;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.world.trees.*;

@UtilityClass
public class TreesRegistry {

    // misc
    public static final TrunkPlacerType<PeachTrunkPlacer> PEACH_TRUNK_PLACER =
            registerTrunk("peach", PeachTrunkPlacer.CODEC);
    public static final FoliagePlacerType<PeachFoliagePlacer> PEACH_FOLIAGE_PLACER =
            registerFoliage("peach", PeachFoliagePlacer.CODEC);
    public static final TreeDecoratorType<PeachWeepingDecorator> PEACH_WEEPING_DECORATOR =
            registerDeco("peach_weeping", PeachWeepingDecorator.CODEC);
    public static final TreeDecoratorType<PeachLanternDecorator> PEACH_LANTERN_DECORATOR =
            registerDeco("peach_lantern", PeachLanternDecorator.CODEC);
    public static final TreeDecoratorType<BirchBranchesDecorator> BIRCH_BRANCHES_DECORATOR =
            registerDeco("birch_branches", BirchBranchesDecorator.CODEC);

    public static void registerTrees() {}

    private static <T extends TreeDecorator> TreeDecoratorType<T> registerDeco(String name, Codec<T> codec) {
        return TreeDecoratorTypeAccessor.callRegister(Etherology.MOD_ID + ":" + name + "_decorator", codec);
    }

    private static <T extends FoliagePlacer> FoliagePlacerType<T> registerFoliage(String name, Codec<T> codec) {
        return FoliagePlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":" + name + "_foliage_placer", codec);
    }

    private static <T extends TrunkPlacer> TrunkPlacerType<T> registerTrunk(String name, Codec<T> codec) {
        return TrunkPlacerTypeAccessor.callRegister(Etherology.MOD_ID + ":" + name + "_trunk_placer", codec);
    }

    public static TreeFeatureConfig.Builder peach() {
        return new TreeFeatureConfig.Builder(
                BlockStateProvider.of(DecoBlocks.PEACH_LOG),
                new PeachTrunkPlacer(11, 5, 3),
                BlockStateProvider.of(DecoBlocks.PEACH_LEAVES),
                new PeachFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1)),
                new TwoLayersFeatureSize(2, 0, 2));
    }
}
