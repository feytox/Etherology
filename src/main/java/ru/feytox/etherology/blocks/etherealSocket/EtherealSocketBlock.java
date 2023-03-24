package ru.feytox.etherology.blocks.etherealSocket;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.registry.RegistrableBlock;

import static ru.feytox.etherology.BlocksRegistry.ETHEREAL_SOCKET_BLOCK_ENTITY;

public class EtherealSocketBlock extends FacingBlock implements RegistrableBlock, BlockEntityProvider {
    protected static final BooleanProperty WITH_GLINT = BooleanProperty.of("with_glint");
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape UP_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;

    public EtherealSocketBlock() {
        super(FabricBlockSettings.of(Material.METAL).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.DOWN)
                .with(WITH_GLINT, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof EtherealSocketBlockEntity socket) {
            return socket.onUse((ServerWorld) world, player, state);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WITH_GLINT);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EtherealSocketBlockEntity socket) {
                ItemScatterer.spawn(world, pos, socket);
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        VoxelShape shape = DOWN_SHAPE;
        switch (facing) {
            case NORTH -> shape = NORTH_SHAPE;
            case SOUTH -> shape = SOUTH_SHAPE;
            case WEST -> shape = WEST_SHAPE;
            case EAST -> shape = EAST_SHAPE;
            case UP -> shape = UP_SHAPE;
        }
        return shape;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        Direction facing = side.equals(Direction.DOWN) || side.equals(Direction.UP) ? side.getOpposite() : side;
        return getDefaultState().with(FACING, facing);
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "ethereal_socket";
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_SOCKET_BLOCK_ENTITY) return null;

        return world.isClient ? EtherealSocketBlockEntity::clientTicker : EtherealSocketBlockEntity::serverTicker;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealSocketBlockEntity(pos, state);
    }

    static {
        DOWN_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(2, 0, 2, 14, 4, 14),
                createCuboidShape(4.5, 4, 4.5, 11.5, 9.5, 11.5),
                BooleanBiFunction.OR
        );
        UP_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(2, 12, 2, 14, 16, 14),
                createCuboidShape(4.5, 6.5, 4.5, 11.5, 12, 11.5),
                BooleanBiFunction.OR
        );
        SOUTH_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(2, 2, 0, 14, 14, 4),
                createCuboidShape(4.5, 4.5, 4, 11.5, 11.5, 9.5),
                BooleanBiFunction.OR
        );
        NORTH_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(2, 2, 12, 14, 14, 16),
                createCuboidShape(4.5, 4.5, 6.5, 11.5, 11.5, 12),
                BooleanBiFunction.OR
        );
        EAST_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 2, 2, 4, 14, 14),
                createCuboidShape(4, 4.5, 4.5, 9.5, 11.5, 11.5),
                BooleanBiFunction.OR
        );
        WEST_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(12, 2, 2, 16, 14, 14),
                createCuboidShape(6.5, 4.5, 4.5, 12, 11.5, 11.5),
                BooleanBiFunction.OR
        );
    }
}
