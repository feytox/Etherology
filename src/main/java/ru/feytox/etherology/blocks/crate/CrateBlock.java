package ru.feytox.etherology.blocks.crate;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
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
import ru.feytox.etherology.ItemsRegistry;
import ru.feytox.etherology.util.registry.RegistrableBlock;

import java.util.List;

public class CrateBlock extends HorizontalFacingBlock implements RegistrableBlock, BlockEntityProvider {
    private static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;


    public CrateBlock() {
        super(FabricBlockSettings.copyOf(Blocks.CHEST).nonOpaque());
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.isSneaking() && player.getMainHandStack().isEmpty() && world.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
                ItemStack crateStack = ItemsRegistry.CARRIED_CRATE.getDefaultStack();
                crate.setStackNbt(crateStack);
                crate.clear();
                crate.markDirty();
                player.setStackInHand(Hand.MAIN_HAND, crateStack);
                world.removeBlock(pos, true);
                return ActionResult.SUCCESS;
            }

            NamedScreenHandlerFactory screenHandlerFactory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !moved) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CrateBlockEntity crate) {
                List<ItemStack> stacks = crate.getItems().subList(0, 4);
                for (int i = 0; i < 4; i++) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stacks.get(i));
                }
                crate.clear();
            }

            super.onStateReplaced(state, world, pos, newState, moved);
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
    public Block getBlockInstance() {
        return this;
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
