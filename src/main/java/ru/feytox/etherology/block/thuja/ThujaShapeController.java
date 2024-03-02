package ru.feytox.etherology.block.thuja;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface ThujaShapeController {

    default BlockState getThujaStateForNeighborUpdate(BlockState state, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!neighborPos.down().equals(pos) && !neighborPos.up().equals(pos)) return state;
        ThujaShape shape = ThujaShape.getShape(world.getBlockState(pos.down()), world.getBlockState(pos.up()));
        return state.with(ThujaBlock.SHAPE, shape);
    }

    default BlockState getThujaPlacementState(BlockState state, ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos currentPos = ctx.getBlockPos();
        ThujaShape shape = ThujaShape.getShape(world.getBlockState(currentPos.down()), world.getBlockState(currentPos.up()));
        return state.with(ThujaBlock.SHAPE, shape);
    }
}
