package ru.feytox.etherology.block.etherealFork;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.block.etherealChannel.EtherealChannelBlock.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_FORK_BLOCK_ENTITY;

public class EtherealForkBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    private static final VoxelShape CENTER_SHAPE;

    public EtherealForkBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(1.0F).nonOpaque());
        setDefaultState(getDefaultState()
                .with(ACTIVATED, false)
                .with(NORTH, PipeSide.EMPTY)
                .with(SOUTH, PipeSide.EMPTY)
                .with(WEST, PipeSide.EMPTY)
                .with(EAST, PipeSide.EMPTY)
                .with(UP, PipeSide.EMPTY)
                .with(DOWN, PipeSide.EMPTY));
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED, NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getForkState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
    }

    public BlockState getForkState(BlockView world, BlockState state, BlockPos pos) {
        List<Direction> directions = new ArrayList<>();
        directions.addAll(Direction.Type.HORIZONTAL.stream().toList());
        directions.addAll(Direction.Type.VERTICAL.stream().toList());

        for (Direction direction: directions) {
            BlockPos checkPos = pos.add(direction.getVector());
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

        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        int weak = neighborState.getWeakRedstonePower(world, neighborPos, direction);
        int strong = neighborState.getStrongRedstonePower(world, neighborPos, direction);
        boolean result = weak > 0 || strong > 0;

        return getForkState(world, this.getDefaultState(), pos).with(ACTIVATED, result);
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
