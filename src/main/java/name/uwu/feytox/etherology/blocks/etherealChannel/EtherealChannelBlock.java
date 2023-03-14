package name.uwu.feytox.etherology.blocks.etherealChannel;

import name.uwu.feytox.etherology.enums.PipeSide;
import name.uwu.feytox.etherology.magic.ether.EtherStorage;
import name.uwu.feytox.etherology.util.registry.RegistrableBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

import static name.uwu.feytox.etherology.BlocksRegistry.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannelBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    protected static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    protected static final DirectionProperty FACING = FacingBlock.FACING;
    protected static final EnumProperty<PipeSide> NORTH = EnumProperty.of("north", PipeSide.class);
    protected static final EnumProperty<PipeSide> EAST = EnumProperty.of("east", PipeSide.class);
    protected static final EnumProperty<PipeSide> WEST = EnumProperty.of("west", PipeSide.class);
    protected static final EnumProperty<PipeSide> SOUTH = EnumProperty.of("south", PipeSide.class);
    protected static final EnumProperty<PipeSide> UP = EnumProperty.of("up", PipeSide.class);
    protected static final EnumProperty<PipeSide> DOWN = EnumProperty.of("down", PipeSide.class);

    public EtherealChannelBlock() {
        super(FabricBlockSettings.of(Material.METAL).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(NORTH, PipeSide.EMPTY)
                .with(SOUTH, PipeSide.EMPTY)
                .with(WEST, PipeSide.EMPTY)
                .with(EAST, PipeSide.EMPTY)
                .with(UP, PipeSide.EMPTY)
                .with(DOWN, PipeSide.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction playerDirection = ctx.getPlayerLookDirection();
        BlockState playerPlacementState = this.getDefaultState().with(FACING, playerDirection);
        BlockState facingState = getFacingState(playerPlacementState);

        return getChannelState(ctx.getWorld(), facingState, ctx.getBlockPos());
    }

    public BlockState getChannelState(BlockView world, BlockState state, BlockPos pos) {
        Iterator<Direction> horizontal = Direction.Type.HORIZONTAL.iterator();
        Iterator<Direction> vertical = Direction.Type.VERTICAL.iterator();

        while (horizontal.hasNext()) {
            Direction direction = horizontal.next();
            state = getDirectionState(world, state, pos, direction);
        }
        while (vertical.hasNext()) {
            Direction direction = vertical.next();
            state = getDirectionState(world, state, pos, direction);
        }

        return state;
    }

    public BlockState getDirectionState(BlockView world, BlockState state, BlockPos pos, Direction direction) {
        BlockPos checkPos = pos.add(direction.getVector());
        if (!(world.getBlockEntity(checkPos) instanceof EtherStorage storage)) return state;

        if (!storage.isOutputSide(direction.getOpposite())) return state;

        EnumProperty<PipeSide> inSide = getAsIn(direction);
        return state.with(inSide, PipeSide.IN);
    }

    public BlockState getFacingState(BlockState state) {
        Direction pipeDirection = state.get(FACING);

        EnumProperty<PipeSide> inSide = getAsIn(pipeDirection);
        EnumProperty<PipeSide> outSide = getAsOut(pipeDirection);

        return this.getDefaultState().with(inSide, PipeSide.IN).with(outSide, PipeSide.OUT);
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
        return getChannelState(world, state, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        boolean result = world.isReceivingRedstonePower(pos);
        world.setBlockState(pos, state.with(ACTIVATED, result));

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
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
}
