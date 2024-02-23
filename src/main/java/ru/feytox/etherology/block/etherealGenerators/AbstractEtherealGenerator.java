package ru.feytox.etherology.block.etherealGenerators;

import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;


public abstract class AbstractEtherealGenerator extends FacingBlock implements RegistrableBlock, BlockEntityProvider {
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape UP_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    public static final BooleanProperty STALLED = BooleanProperty.of("stalled");
    @Getter
    private final String blockId;
    @Getter
    private final int minCooldown;
    @Getter
    private final int maxCooldown;
    private final float stopChance;

    protected AbstractEtherealGenerator(Settings settings, String blockId, int minCooldown, int maxCooldown, float stopChance) {
        super(settings.nonOpaque());
        this.blockId = blockId;
        this.minCooldown = minCooldown;
        this.maxCooldown = maxCooldown;
        this.stopChance = stopChance;
        setDefaultState(getDefaultState()
                .with(FACING, Direction.DOWN)
                .with(STALLED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient || !state.get(STALLED)) return super.onUse(state, world, pos, player, hand, hit);

        // TODO: 31/03/2023 добавить более логичную очистку
        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
        if (!handStack.isOf(EItems.THUJA_OIL)) return ActionResult.FAIL;

        if (world.getBlockEntity(pos) instanceof AbstractEtherealGeneratorBlockEntity generator) {
            handStack.decrement(1);
            generator.unstall((ServerWorld) world, state);
            return ActionResult.SUCCESS;
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STALLED);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != getBlockEntityType()) return null;

        return world.isClient ? AbstractEtherealGeneratorBlockEntity::clientTicker : AbstractEtherealGeneratorBlockEntity::serverTicker;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        Direction facing = side.equals(Direction.DOWN) || side.equals(Direction.UP) ? side.getOpposite() : side;
        return getDefaultState().with(FACING, facing);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        VoxelShape shape = getDownShape();
        switch (facing) {
            case NORTH -> shape = getNorthShape();
            case SOUTH -> shape = getSouthShape();
            case WEST -> shape = getWestShape();
            case EAST -> shape = getEastShape();
            case UP -> shape = getUpShape();
        }
        return shape;
    }

    public abstract BlockEntityType<?> getBlockEntityType();

    public VoxelShape getDownShape() {
        return DOWN_SHAPE;
    }
    public VoxelShape getUpShape() {
        return UP_SHAPE;
    }
    public VoxelShape getNorthShape() {
        return NORTH_SHAPE;
    }
    public VoxelShape getSouthShape() {
        return SOUTH_SHAPE;
    }
    public VoxelShape getEastShape() {
        return EAST_SHAPE;
    }
    public VoxelShape getWestShape() {
        return WEST_SHAPE;
    }

    public float getStopChance(boolean isInZone) {
        return isInZone ? stopChance * 1.5f : stopChance;
    }

    static {
        DOWN_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 0, 16, 6, 16),
                createCuboidShape(5, 6, 5, 11, 8, 11),
                BooleanBiFunction.OR
        );
        UP_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 10, 0, 16, 16, 16),
                createCuboidShape(5, 8, 5, 11, 10, 11),
                BooleanBiFunction.OR
        );
        NORTH_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 10, 16, 16, 16),
                createCuboidShape(5, 5, 8, 11, 11, 10),
                BooleanBiFunction.OR
        );
        SOUTH_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 0, 16, 16, 6),
                createCuboidShape(5, 5, 6, 11, 11, 8),
                BooleanBiFunction.OR
        );
        EAST_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(0, 0, 0, 6, 16, 16),
                createCuboidShape(6, 5, 5, 8, 11, 11),
                BooleanBiFunction.OR
        );
        WEST_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(10, 0, 0, 16, 16, 16),
                createCuboidShape(8, 5, 5, 10, 11, 11),
                BooleanBiFunction.OR
        );
    }
}
