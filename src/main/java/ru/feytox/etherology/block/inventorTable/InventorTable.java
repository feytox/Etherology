package ru.feytox.etherology.block.inventorTable;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

public class InventorTable extends Block implements RegistrableBlock {

    private static final VoxelShape OUTLINE_SHAPE;

    public InventorTable() {
        super(FabricBlockSettings.copy(Blocks.SMITHING_TABLE).nonOpaque());
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
        if (!world.isClient) player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.success(world.isClient);
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) ->
                new InventorTableScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)),
                Text.translatable(getTranslationKey()));
    }

    @Override
    public String getBlockId() {
        return "inventor_table";
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                Block.createCuboidShape(0, 13, 0, 16, 16, 16),
                VoxelShapes.combineAndSimplify(
                        Block.createCuboidShape(2, 3, 2, 14, 16, 14),
                        VoxelShapes.combineAndSimplify(
                                VoxelShapes.combineAndSimplify(
                                        Block.createCuboidShape(1, 0, 1, 4, 16, 4),
                                        Block.createCuboidShape(12, 0, 12, 15, 16, 15),
                                        BooleanBiFunction.OR
                                ),
                                VoxelShapes.combineAndSimplify(
                                        Block.createCuboidShape(1, 0, 12, 4, 16, 15),
                                        Block.createCuboidShape(12, 0, 1, 15, 16, 4),
                                        BooleanBiFunction.OR
                                ),
                                BooleanBiFunction.OR
                        ),
                        BooleanBiFunction.OR
                ),
                BooleanBiFunction.OR
        );
    }
}
