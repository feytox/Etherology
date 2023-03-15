package name.uwu.feytox.etherology.blocks.etherealChannel;

import name.uwu.feytox.etherology.enums.PipeSide;
import name.uwu.feytox.etherology.magic.ether.EtherStorage;
import name.uwu.feytox.etherology.util.registry.RegistrableBlock;
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
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static name.uwu.feytox.etherology.BlocksRegistry.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannelBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    protected static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    protected static final DirectionProperty FACING = FacingBlock.FACING;
    protected static final BooleanProperty IS_CROSS = BooleanProperty.of("is_cross");
    protected static final EnumProperty<PipeSide> NORTH = EnumProperty.of("north", PipeSide.class);
    protected static final EnumProperty<PipeSide> EAST = EnumProperty.of("east", PipeSide.class);
    protected static final EnumProperty<PipeSide> WEST = EnumProperty.of("west", PipeSide.class);
    protected static final EnumProperty<PipeSide> SOUTH = EnumProperty.of("south", PipeSide.class);
    protected static final EnumProperty<PipeSide> UP = EnumProperty.of("up", PipeSide.class);
    protected static final EnumProperty<PipeSide> DOWN = EnumProperty.of("down", PipeSide.class);

    private static final VoxelShape CENTER_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape UP_SHAPE;

    public EtherealChannelBlock() {
        super(FabricBlockSettings.of(Material.METAL).nonOpaque());
        setDefaultState(getDefaultState()
                .with(ACTIVATED, false)
                .with(FACING, Direction.NORTH)
                .with(IS_CROSS, false)
                .with(NORTH, PipeSide.EMPTY)
                .with(SOUTH, PipeSide.EMPTY)
                .with(WEST, PipeSide.EMPTY)
                .with(EAST, PipeSide.EMPTY)
                .with(UP, PipeSide.EMPTY)
                .with(DOWN, PipeSide.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVATED, IS_CROSS, NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        List<VoxelShape> shapes = new ArrayList<>();
        if (!state.get(NORTH).equals(PipeSide.EMPTY)) shapes.add(NORTH_SHAPE);
        if (!state.get(SOUTH).equals(PipeSide.EMPTY)) shapes.add(SOUTH_SHAPE);
        if (!state.get(EAST).equals(PipeSide.EMPTY)) shapes.add(EAST_SHAPE);
        if (!state.get(WEST).equals(PipeSide.EMPTY)) shapes.add(WEST_SHAPE);
        if (!state.get(UP).equals(PipeSide.EMPTY)) shapes.add(UP_SHAPE);
        if (!state.get(DOWN).equals(PipeSide.EMPTY)) shapes.add(DOWN_SHAPE);
        VoxelShape branchShape = shapes.stream().reduce(CENTER_SHAPE, VoxelShapes::union);

        return VoxelShapes.combineAndSimplify(CENTER_SHAPE, branchShape, BooleanBiFunction.OR);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof EtherealChannelBlockEntity channel) {
                channel.onUse((ServerWorld) world);
                return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction playerDirection = ctx.getPlayerLookDirection();
        BlockState playerPlacementState = this.getDefaultState().with(FACING, playerDirection);

        return getChannelState(ctx.getWorld(), playerPlacementState, ctx.getBlockPos());
    }

    public BlockState getChannelState(BlockView world, BlockState state, BlockPos pos) {
        Direction pipeDirection = state.get(FACING);
        state = this.getDefaultState().with(FACING, pipeDirection);
        Iterator<Direction> horizontal = Direction.Type.HORIZONTAL.iterator();
        Iterator<Direction> vertical = Direction.Type.VERTICAL.iterator();

        int inputCount = 0;
        while (horizontal.hasNext()) {
            Direction direction = horizontal.next();
            boolean result = isNeighborOutput(world, pos, direction);
            if (!result) continue;

            inputCount++;
            EnumProperty<PipeSide> inSide = getAsIn(direction.getOpposite());
            state = state.with(inSide, PipeSide.IN);
        }
        while (vertical.hasNext()) {
            Direction direction = vertical.next();
            boolean result = isNeighborOutput(world, pos, direction);
            if (!result) continue;

            inputCount++;
            EnumProperty<PipeSide> inSide = getAsIn(direction.getOpposite());
            state = state.with(inSide, PipeSide.IN);
        }

        return getFacingState(state, inputCount);
    }

    public boolean isNeighborOutput(BlockView world, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.add(direction.getVector());
        if (!(world.getBlockEntity(checkPos) instanceof EtherStorage storage)) return false;

        return storage.isOutputSide(direction.getOpposite());
    }

    public BlockState getFacingState(BlockState state, int inputCount) {
        Direction pipeDirection = state.get(FACING);
        EnumProperty<PipeSide> outSide = getAsOut(pipeDirection);
        BlockState blockState = state.with(outSide, PipeSide.OUT);

        EnumProperty<PipeSide> inSide = getAsIn(pipeDirection);
        if (inputCount == 0) {
            blockState = blockState.with(inSide, PipeSide.IN);
        } else if (inputCount > 1 || !blockState.get(inSide).isInput()) {
            blockState = blockState.with(IS_CROSS, true);
        }

        return blockState;
    }

    private static EnumProperty<PipeSide> getAsIn(Direction direction) {
        switch (direction) {
            case SOUTH -> {
                return NORTH;
            }
            case WEST -> {
                return EAST;
            }
            case EAST -> {
                return WEST;
            }
            case DOWN -> {
                return UP;
            }
            case UP -> {
                return DOWN;
            }
            default -> {
                return SOUTH;
            }
        }
    }

    private static EnumProperty<PipeSide> getAsOut(Direction direction) {
        switch (direction) {
            case SOUTH -> {
                return SOUTH;
            }
            case WEST -> {
                return WEST;
            }
            case EAST -> {
                return EAST;
            }
            case DOWN -> {
                return DOWN;
            }
            case UP -> {
                return UP;
            }
            default -> {
                return NORTH;
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        int weak = neighborState.getWeakRedstonePower(world, neighborPos, direction);
        int strong = neighborState.getStrongRedstonePower(world, neighborPos, direction);
        boolean result = weak > 0 || strong > 0;

        return getChannelState(world, state, pos).with(ACTIVATED, result);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_CHANNEL_BLOCK_ENTITY) return null;

        return world.isClient ? null : EtherealChannelBlockEntity::serverTicker;
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "ethereal_channel";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealChannelBlockEntity(pos, state);
    }

    static {
        CENTER_SHAPE = createCuboidShape(5, 5, 5, 11, 11, 11);
        NORTH_SHAPE = createCuboidShape(5, 5, 0, 11, 11, 5);
        SOUTH_SHAPE = createCuboidShape(5, 5, 11, 11, 11, 16);
        WEST_SHAPE = createCuboidShape(0, 5, 5, 5, 11, 11);
        EAST_SHAPE = createCuboidShape(11, 5, 5, 16, 11, 11);
        UP_SHAPE = createCuboidShape(5, 11, 5, 11, 16, 11);
        DOWN_SHAPE = createCuboidShape(5, 0, 5, 11, 5, 11);
    }
}
