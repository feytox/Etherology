package ru.feytox.etherology.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import ru.feytox.etherology.block.thuja.ThujaShape;
import ru.feytox.etherology.registry.block.DecoBlocks;

import static ru.feytox.etherology.block.thuja.ThujaBlock.SHAPE;

public class ThujaFeature extends Feature<DefaultFeatureConfig> {

    public ThujaFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        if (isNotSuitable(world, pos)) return false;

        Random random = context.getRandom();
        BlockState state = DecoBlocks.THUJA.getDefaultState();
        BlockState plantState = DecoBlocks.THUJA_PLANT.getDefaultState();
        int height = random.nextBetween(1, 4);
        for (int i = 0; i < height; i++) {
            if (i == height-1 || !world.isAir(pos.up())) {
                world.setBlockState(pos, state.with(SHAPE, i == 0 ? ThujaShape.BUSH : ThujaShape.CROWN), Block.NO_REDRAW);
                return true;
            }
            world.setBlockState(pos, plantState.with(SHAPE, i == 0 ? ThujaShape.ROOT : ThujaShape.STEM), Block.NO_REDRAW);
            pos = pos.up();
        }
        return true;
    }

    private static boolean isNotSuitable(WorldAccess world, BlockPos pos) {
        return !world.isAir(pos) || !DecoBlocks.THUJA.canPlaceAt(DecoBlocks.THUJA.getDefaultState(), world, pos);
    }
}
