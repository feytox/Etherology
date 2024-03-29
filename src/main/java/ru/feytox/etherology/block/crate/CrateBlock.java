package ru.feytox.etherology.block.crate;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

public class CrateBlock extends HorizontalFacingBlock implements RegistrableBlock, BlockEntityProvider {

    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;


    public CrateBlock() {
        super(FabricBlockSettings.copy(Blocks.CHEST).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH));
    }

    @Override
    public Item asItem() {
        return EBlocks.CRATE.getItem();
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH, SOUTH -> NORTH_SHAPE;
            case WEST, EAST -> WEST_SHAPE;
            default -> super.getOutlineShape(state, world, pos, context);
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
        if (screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);
        return ActionResult.SUCCESS;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return EBlocks.CRATE.getItem().getDefaultStack();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !moved) {
            dropInventory(world, pos);
            super.onStateReplaced(state, world, pos, newState, false);
        }
    }

    private void dropInventory(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof CrateBlockEntity crate) {
            ItemScatterer.spawn(world, pos, crate);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    public String getBlockId() {
        return "crate";
    }

    static {
        NORTH_SHAPE = createCuboidShape(1, 0, 3, 15, 10, 13);
        WEST_SHAPE = createCuboidShape(3, 0, 1, 13, 10, 15);
    }
}
