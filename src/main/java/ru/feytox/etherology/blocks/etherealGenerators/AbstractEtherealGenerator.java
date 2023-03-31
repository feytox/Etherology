package ru.feytox.etherology.blocks.etherealGenerators;

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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.registry.RegistrableBlock;


public abstract class AbstractEtherealGenerator extends FacingBlock implements RegistrableBlock, BlockEntityProvider {
    public static final BooleanProperty STALLED = BooleanProperty.of("stalled");
    private final String blockId;
    private final int minCooldown;
    private final int maxCooldown;
    private final float stopChance;

    protected AbstractEtherealGenerator(Settings settings, String blockId, int minCooldown, int maxCooldown, float stopChance) {
        super(settings);
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
        if (world.isClient) return super.onUse(state, world, pos, player, hand, hit);

        // TODO: 31/03/2023 добавить более логичную очистку
        if (world.getBlockEntity(pos) instanceof AbstractEtherealGeneratorBlockEntity generator) {
            generator.unstall((ServerWorld) world, state);
            return ActionResult.CONSUME_PARTIAL;
        }

        return ActionResult.SUCCESS;
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
    public abstract VoxelShape getDownShape();
    public abstract VoxelShape getUpShape();
    public abstract VoxelShape getNorthShape();
    public abstract VoxelShape getSouthShape();
    public abstract VoxelShape getEastShape();
    public abstract VoxelShape getWestShape();

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return blockId;
    }

    public int getMinCooldown() {
        return minCooldown;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    public float getStopChance(boolean isInZone) {
        return isInZone ? stopChance * 1.5f : stopChance;
    }
}
