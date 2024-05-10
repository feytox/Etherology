package ru.feytox.etherology.block.tuningFork;

import lombok.val;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class TuningFork extends FacingBlock implements RegistrableBlock, BlockEntityProvider, Waterloggable {

    public static final EnumProperty<Direction> VERTICAL_FACING = DirectionProperty.of("vertical_facing");
    private static final EnumProperty<Direction> HORIZONTAL_FACING = DirectionProperty.of("horizontal_facing");
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final IntProperty NOTE = Properties.NOTE;
    public static final BooleanProperty RESONATING = BooleanProperty.of("resonating");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape UP_NORTH_SOUTH_SHAPE;
    private static final VoxelShape UP_WEST_EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape DOWN_NORTH_SOUTH_SHAPE;
    private static final VoxelShape DOWN_WEST_EAST_SHAPE;

    public TuningFork() {
        super(FabricBlockSettings.of(Material.METAL).strength(1.5F).nonOpaque());
        setDefaultState(getDefaultState()
                .with(VERTICAL_FACING, Direction.UP)
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(NOTE, 0)
                .with(RESONATING, false)
                .with(WATERLOGGED, false)
        );
    }

    /**
     * @see NoteBlock#onUse(BlockState, World, BlockPos, PlayerEntity, Hand, BlockHitResult)
     */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        state = state.cycle(NOTE);
        world.setBlockState(pos, state, NOTIFY_ALL);
        world.addSyncedBlockEvent(pos, this, 0, 0);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        int note = state.get(NOTE);
        world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, note / 24.0, 0.0, 0.0);
        if (!state.get(WATERLOGGED)) {
            float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
            world.playSound(null, pos, EtherSounds.TUNING_FORK_TUNING, SoundCategory.BLOCKS, 0.5f, pitch);
        }
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!(sourceBlock instanceof NoteBlock)) return;
        Direction bottomFacing = state.get(VERTICAL_FACING).getOpposite();
        if (!pos.add(bottomFacing.getVector()).equals(sourcePos)) return;

        int power = world.getReceivedRedstonePower(sourcePos);
        boolean powered = power > 0;
        boolean wasPowered = state.get(POWERED);
        if (powered == wasPowered) return;

        state = state.with(POWERED, powered);
        world.setBlockState(pos, state);
        if (!(world instanceof ServerWorld serverWorld) || !(world.getBlockEntity(pos) instanceof TuningForkBlockEntity fork)) return;

        if (powered) fork.tryActivate(serverWorld, state, false, -1);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(RESONATING) ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(RESONATING) && direction.equals(state.get(VERTICAL_FACING)) ? 15 : 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
        if (blockEntity instanceof TuningForkBlockEntity fork) return fork;
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL_FACING, HORIZONTAL_FACING, POWERED, NOTE, RESONATING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        int power = ctx.getWorld().getReceivedRedstonePower(ctx.getBlockPos());
        boolean powered = power > 0;
        return getDefaultState().with(VERTICAL_FACING, ctx.getSide())
                .with(HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite())
                .with(POWERED, powered).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TuningForkBlockEntity.getTicker(world, type, EBlocks.TUNING_FORK_BLOCK_ENTITY);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        val verticalFacing = state.get(VERTICAL_FACING);
        val horizontalFacing = state.get(HORIZONTAL_FACING);
        val isNorthSouth = horizontalFacing.equals(Direction.NORTH) || horizontalFacing.equals(Direction.SOUTH);
        return switch (verticalFacing) {
            case UP -> {
                if (isNorthSouth)
                    yield UP_NORTH_SOUTH_SHAPE;
                yield UP_WEST_EAST_SHAPE;
            }
            case DOWN -> {
                if (isNorthSouth)
                    yield DOWN_NORTH_SOUTH_SHAPE;
                yield DOWN_WEST_EAST_SHAPE;
            }
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    public String getBlockId() {
        return "tuning_fork";
    }

    static {
        UP_NORTH_SOUTH_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(5, 0, 5, 11, 4, 11),
                Block.createCuboidShape(7, 4, 7, 9, 6, 9),
                Block.createCuboidShape(5, 6, 7, 7, 16, 9),
                Block.createCuboidShape(9, 6, 7, 11, 16, 9),
                Block.createCuboidShape(5, 6, 7, 11, 8, 9)
        ).simplify();
        UP_WEST_EAST_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(5, 0, 5, 11, 4, 11),
                Block.createCuboidShape(7, 4, 7, 9, 6, 9),
                Block.createCuboidShape(7, 6, 9, 9, 16, 11),
                Block.createCuboidShape(7, 6, 5, 9, 16, 7),
                Block.createCuboidShape(7, 6, 5, 9, 8, 11)
        ).simplify();
        NORTH_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(5, 5, 12, 11, 11, 16),
                Block.createCuboidShape(7, 7, 10, 9, 9, 12),
                Block.createCuboidShape(5, 7, 0, 7, 9, 10),
                Block.createCuboidShape(9, 7, 0, 11, 9, 10),
                Block.createCuboidShape(5, 7, 8, 11, 9, 10)
        ).simplify();
        SOUTH_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(5, 5, 0, 11, 11, 4),
                Block.createCuboidShape(7, 7, 4, 9, 9, 6),
                Block.createCuboidShape(9, 7, 6, 11, 9, 16),
                Block.createCuboidShape(5, 7, 6, 7, 9, 16),
                Block.createCuboidShape(5, 7, 6, 11, 9, 8)
        ).simplify();
        WEST_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(12, 5, 5, 16, 11, 11),
                Block.createCuboidShape(10, 7, 7, 12, 9, 9),
                Block.createCuboidShape(0, 7, 9, 10, 9, 11),
                Block.createCuboidShape(0, 7, 5, 10, 9, 7),
                Block.createCuboidShape(8, 7, 5, 10, 9, 11)
        ).simplify();
        EAST_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(0, 5, 5, 4, 11, 11),
                Block.createCuboidShape(4, 7, 7, 6, 9, 9),
                Block.createCuboidShape(6, 7, 9, 16, 9, 11),
                Block.createCuboidShape(6, 7, 5, 16, 9, 7),
                Block.createCuboidShape(6, 7, 5, 8, 9, 11)
        ).simplify();
        DOWN_NORTH_SOUTH_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(5, 12, 5, 11, 16, 11),
                Block.createCuboidShape(7, 10, 7, 9, 12, 9),
                Block.createCuboidShape(5, 0, 7, 7, 10, 9),
                Block.createCuboidShape(9, 0, 7, 11, 10, 9),
                Block.createCuboidShape(5, 8, 7, 11, 10, 9)
        ).simplify();
        DOWN_WEST_EAST_SHAPE = VoxelShapes.union(
                Block.createCuboidShape(5, 12, 5, 11, 16, 11),
                Block.createCuboidShape(7, 10, 7, 9, 12, 9),
                Block.createCuboidShape(7, 0, 9, 9, 10, 11),
                Block.createCuboidShape(7, 0, 5, 9, 10, 7),
                Block.createCuboidShape(7, 8, 5, 9, 10, 11)
        ).simplify();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TuningForkBlockEntity(pos, state);
    }
}
