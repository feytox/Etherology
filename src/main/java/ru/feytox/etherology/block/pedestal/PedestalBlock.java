package ru.feytox.etherology.block.pedestal;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
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
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.PedestalShape;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.interaction.RemoveBlockEntityS2C;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import java.util.Optional;

public class PedestalBlock extends HorizontalFacingBlock implements BlockEntityProvider, RegistrableBlock {

    public static final EnumProperty<PedestalShape> SHAPE = EnumProperty.of("shape", PedestalShape.class);
    public static final BooleanProperty DECORATION = BooleanProperty.of("decoration");
    public static final EnumProperty<DyeColor> CLOTH_COLOR = EnumProperty.of("cloth_color", DyeColor.class);
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape FULL_SHAPE;

    public PedestalBlock() {
        super(FabricBlockSettings.of(Material.STONE).strength(3.0f).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(SHAPE, PedestalShape.FULL)
                .with(DECORATION, false)
                .with(CLOTH_COLOR, DyeColor.WHITE)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, DECORATION, CLOTH_COLOR, FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return state.get(SHAPE).isHasItem() ? new PedestalBlockEntity(pos, state) : null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!neighborPos.down().equals(pos) && !neighborPos.up().equals(pos)) return state;
        PedestalShape shape = PedestalShape.getShape(world.getBlockState(pos.down()), world.getBlockState(pos.up()));
        if (!shape.isHasItem()) state = state.with(CLOTH_COLOR, DyeColor.WHITE).with(DECORATION, false);
        return state.with(SHAPE, shape);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos currentPos = ctx.getBlockPos();
        PedestalShape shape = PedestalShape.getShape(world.getBlockState(currentPos.down()), world.getBlockState(currentPos.up()));
        return getDefaultState().with(SHAPE, shape);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            PedestalBlockEntity pedestalBlockEntity = (PedestalBlockEntity) world.getBlockEntity(pos);
            if (pedestalBlockEntity != null) {
                pedestalBlockEntity.interact((ServerWorld) world, state, player, hand);
                pedestalBlockEntity.syncData((ServerWorld) world);
            }
        }


        return ActionResult.CONSUME;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(SHAPE)) {
            case BOTTOM -> BOTTOM_SHAPE;
            case MIDDLE -> MIDDLE_SHAPE;
            case TOP -> TOP_SHAPE;
            default -> FULL_SHAPE;
        };
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!(world.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal)) return;

        Optional<PedestalShape> oldShape = state.getOrEmpty(SHAPE);
        Optional<PedestalShape> newShape = newState.getOrEmpty(SHAPE);
        if (oldShape.isEmpty()) return;

        if ((oldShape.get().isHasItem() && (newShape.isEmpty() || !newShape.get().isHasItem())) || !state.isOf(newState.getBlock())) {
            ItemScatterer.spawn(world, pos.up(), pedestal);
            world.removeBlockEntity(pos);
            EtherologyNetwork.sendForTracking(new RemoveBlockEntityS2C(pos), pedestal);
        }
    }

    static {
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

        BOTTOM_SHAPE = VoxelShapes.combineAndSimplify(
                MIDDLE_SHAPE,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 3.0, 13.0),
                BooleanBiFunction.OR
        );

        TOP_SHAPE = VoxelShapes.combineAndSimplify(
                MIDDLE_SHAPE,
                Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0),
                BooleanBiFunction.OR
        );

        FULL_SHAPE = VoxelShapes.combineAndSimplify(
                BOTTOM_SHAPE,
                TOP_SHAPE,
                BooleanBiFunction.OR
        );
    }

    @Override
    public String getBlockId() {
        return "pedestal";
    }
}
