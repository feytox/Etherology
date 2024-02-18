package ru.feytox.etherology.block.jewelryTable;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import static ru.feytox.etherology.registry.block.EBlocks.JEWELRY_TABLE_BLOCK_ENTITY;

public class JewelryTable extends Block implements RegistrableBlock, BlockEntityProvider {

    private static final VoxelShape OUTLINE_SHAPE;

    public JewelryTable() {
        super(FabricBlockSettings.copy(Blocks.STONE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return OUTLINE_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) return;
        if (!(world.getBlockEntity(pos) instanceof JewelryBlockEntity table)) return;

        ItemScatterer.spawn(world, pos.up(), table);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public String getBlockId() {
        return "jewelry_table";
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                VoxelShapes.combineAndSimplify(
                        createCuboidShape(0, 0, 0, 16, 4, 16),
                        createCuboidShape(1, 3, 1, 15, 9, 15),
                        BooleanBiFunction.OR
                ),
                createCuboidShape(0, 8, 0, 16, 16, 16),
                BooleanBiFunction.OR
        );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return JewelryBlockEntity.getTicker(world, type, JEWELRY_TABLE_BLOCK_ENTITY);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JewelryBlockEntity(pos, state);
    }
}
