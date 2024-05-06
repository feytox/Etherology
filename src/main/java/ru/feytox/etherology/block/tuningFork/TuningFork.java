package ru.feytox.etherology.block.tuningFork;

import lombok.val;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class TuningFork extends FacingBlock implements RegistrableBlock {

    private static final EnumProperty<Direction> VERTICAL_FACING = DirectionProperty.of("vertical_facing");
    private static final EnumProperty<Direction> HORIZONTAL_FACING = DirectionProperty.of("horizontal_facing");
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final IntProperty NOTE = Properties.NOTE;
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
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VERTICAL_FACING, HORIZONTAL_FACING, POWERED, NOTE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(VERTICAL_FACING, ctx.getSide()).with(HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
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
}
