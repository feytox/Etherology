package ru.feytox.etherology.block.etherealFork;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.PipeSide;
import ru.feytox.etherology.magic.ether.EtherFork;
import ru.feytox.etherology.magic.ether.EtherPipe;
import ru.feytox.etherology.util.misc.RegistrableBlock;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.state.property.Properties.WATERLOGGED;
import static ru.feytox.etherology.block.etherealChannel.EtherealChannel.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_FORK_BLOCK_ENTITY;

public class EtherealForkBlock extends Block implements RegistrableBlock, BlockEntityProvider, Waterloggable {

    private static final VoxelShape CENTER_SHAPE;

    public EtherealForkBlock() {
        super(Settings.create().mapColor(MapColor.BROWN).strength(1.0F).nonOpaque());
        setDefaultState(getDefaultState()
                .with(ACTIVATED, false)
                .with(NORTH, PipeSide.EMPTY)
                .with(SOUTH, PipeSide.EMPTY)
                .with(WEST, PipeSide.EMPTY)
                .with(EAST, PipeSide.EMPTY)
                .with(UP, PipeSide.EMPTY)
                .with(DOWN, PipeSide.EMPTY)
                .with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        List<VoxelShape> shapes = new ArrayList<>();
        return getShape(state, shapes, CENTER_SHAPE);
    }

    public static VoxelShape getShape(BlockState state, List<VoxelShape> shapes, VoxelShape centerShape) {
        if (!state.get(NORTH).equals(PipeSide.EMPTY)) shapes.add(NORTH_SHAPE);
        if (!state.get(SOUTH).equals(PipeSide.EMPTY)) shapes.add(SOUTH_SHAPE);
        if (!state.get(EAST).equals(PipeSide.EMPTY)) shapes.add(EAST_SHAPE);
        if (!state.get(WEST).equals(PipeSide.EMPTY)) shapes.add(WEST_SHAPE);
        if (!state.get(UP).equals(PipeSide.EMPTY)) shapes.add(UP_SHAPE);
        if (!state.get(DOWN).equals(PipeSide.EMPTY)) shapes.add(DOWN_SHAPE);
        VoxelShape branchShape = shapes.stream().reduce(centerShape, VoxelShapes::union);

        return VoxelShapes.combineAndSimplify(centerShape, branchShape, BooleanBiFunction.OR);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED, NORTH, SOUTH, WEST, EAST, UP, DOWN, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getForkState(ctx.getWorld(), getDefaultState(), ctx.getBlockPos());
    }

    public BlockState getForkState(BlockView world, BlockState state, BlockPos pos) {
        var directions = new ArrayList<Direction>();
        directions.addAll(Direction.Type.HORIZONTAL.stream().toList());
        directions.addAll(Direction.Type.VERTICAL.stream().toList());

        for (var direction: directions) {
            var checkPos = pos.add(direction.getVector());
            if (!(world.getBlockEntity(checkPos) instanceof EtherPipe pipe)) continue;
            if (pipe instanceof EtherFork) continue;

            if (pipe.isOutputSide(direction.getOpposite())) {
                EnumProperty<PipeSide> inSide = getAsIn(direction.getOpposite());
                state = state.with(inSide, PipeSide.IN);
            } else {
                EnumProperty<PipeSide> outSide = getAsOut(direction);
                state = state.with(outSide, PipeSide.OUT);
            }
        }

        var fluid = world.getFluidState(pos).getFluid();
        return state.with(WATERLOGGED, fluid == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        int weak = neighborState.getWeakRedstonePower(world, neighborPos, direction);
        int strong = neighborState.getStrongRedstonePower(world, neighborPos, direction);
        boolean result = weak > 0 || strong > 0;

        return getForkState(world, this.getDefaultState(), pos).with(ACTIVATED, result);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public String getBlockId() {
        return "ethereal_fork";
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_FORK_BLOCK_ENTITY) return null;

        return world.isClient ? null : EtherealForkBlockEntity::serverTicker;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealForkBlockEntity(pos, state);
    }

    static {
        CENTER_SHAPE = createCuboidShape(4, 4, 4, 12, 12, 12);
    }
}
