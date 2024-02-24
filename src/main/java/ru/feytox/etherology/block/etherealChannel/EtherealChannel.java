package ru.feytox.etherology.block.etherealChannel;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.PipeSide;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL_BLOCK_ENTITY;

public class EtherealChannel extends Block implements RegistrableBlock, BlockEntityProvider {
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty IN_CASE = BooleanProperty.of("in_case");
    public static final BooleanProperty IS_CROSS = BooleanProperty.of("is_cross");
    public static final EnumProperty<PipeSide> NORTH = EnumProperty.of("north", PipeSide.class);
    public static final EnumProperty<PipeSide> EAST = EnumProperty.of("east", PipeSide.class);
    public static final EnumProperty<PipeSide> WEST = EnumProperty.of("west", PipeSide.class);
    public static final EnumProperty<PipeSide> SOUTH = EnumProperty.of("south", PipeSide.class);
    public static final EnumProperty<PipeSide> UP = EnumProperty.of("up", PipeSide.class);
    public static final EnumProperty<PipeSide> DOWN = EnumProperty.of("down", PipeSide.class);

    private static final VoxelShape CENTER_SHAPE;
    public static final VoxelShape NORTH_SHAPE;
    public static final VoxelShape SOUTH_SHAPE;
    public static final VoxelShape EAST_SHAPE;
    public static final VoxelShape WEST_SHAPE;
    public static final VoxelShape DOWN_SHAPE;
    public static final VoxelShape UP_SHAPE;

    public EtherealChannel() {
        super(FabricBlockSettings.of(Material.METAL).strength(1.0f).nonOpaque());
        setDefaultState(getDefaultState()
                .with(ACTIVATED, false)
                .with(FACING, Direction.NORTH)
                .with(IN_CASE, false)
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
        builder.add(FACING, ACTIVATED, IN_CASE, IS_CROSS, NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(IN_CASE)) return super.onUse(state, world, pos, player, hand, hit);
        ItemStack handStack = player.getStackInHand(hand);
        if (!handStack.isOf(EBlocks.ETHEREAL_CHANNEL_CASE.getItem())) return super.onUse(state, world, pos, player, hand, hit);

        if (!player.isCreative()) handStack.decrement(1);
        world.setBlockState(pos, state.with(IN_CASE, true));
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        tryDropCaseOnBreak(state, world, pos, newState);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void tryDropCaseOnBreak(BlockState state, World world, BlockPos pos, BlockState newState) {
        if (state.isOf(newState.getBlock())) return;
        if (!state.get(IN_CASE)) return;

        Vec3d dropPos = pos.toCenterPos();
        ItemScatterer.spawn(world, dropPos.x, dropPos.y, dropPos.z, EBlocks.ETHEREAL_CHANNEL_CASE.getItem().getDefaultStack());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(IN_CASE)) return VoxelShapes.fullCube();
        List<VoxelShape> shapes = new ObjectArrayList<>();
        if (!state.get(NORTH).equals(PipeSide.EMPTY)) shapes.add(NORTH_SHAPE);
        if (!state.get(SOUTH).equals(PipeSide.EMPTY)) shapes.add(SOUTH_SHAPE);
        if (!state.get(EAST).equals(PipeSide.EMPTY)) shapes.add(EAST_SHAPE);
        if (!state.get(WEST).equals(PipeSide.EMPTY)) shapes.add(WEST_SHAPE);
        if (!state.get(UP).equals(PipeSide.EMPTY)) shapes.add(UP_SHAPE);
        if (!state.get(DOWN).equals(PipeSide.EMPTY)) shapes.add(DOWN_SHAPE);
        VoxelShape branchShape = shapes.stream().reduce(CENTER_SHAPE, VoxelShapes::union);

        return VoxelShapes.combineAndSimplify(CENTER_SHAPE, branchShape, BooleanBiFunction.OR);
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
        boolean inCase = state.get(IN_CASE);
        state = getDefaultState().with(FACING, pipeDirection).with(IN_CASE, inCase);
        List<Direction> directions = new ArrayList<>();
        directions.addAll(Direction.Type.HORIZONTAL.stream().toList());
        directions.addAll(Direction.Type.VERTICAL.stream().toList());

        int inputCount = 0;
        for (Direction direction: directions) {
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

    public static EnumProperty<PipeSide> getAsIn(Direction direction) {
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

    public static EnumProperty<PipeSide> getAsOut(Direction direction) {
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
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        int power = world.getReceivedRedstonePower(pos);
        boolean result = power > 0;
        world.setBlockState(pos, getChannelState(world, state, pos).with(ACTIVATED, result));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_CHANNEL_BLOCK_ENTITY) return null;

        return world.isClient ? EtherealChannelPipeBlockEntity::clientTicker : EtherealChannelPipeBlockEntity::serverTicker;
    }

    @Override
    public String getBlockId() {
        return "ethereal_channel";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealChannelPipeBlockEntity(pos, state);
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
