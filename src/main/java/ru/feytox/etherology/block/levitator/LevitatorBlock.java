package ru.feytox.etherology.block.levitator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

import static net.minecraft.state.property.Properties.POWER;
import static net.minecraft.state.property.Properties.POWERED;
import static ru.feytox.etherology.registry.block.EBlocks.LEVITATOR_BLOCK_ENTITY;

@SuppressWarnings("deprecation")
public class LevitatorBlock extends FacingBlock implements RegistrableBlock, BlockEntityProvider {
    protected static final BooleanProperty PUSHING = BooleanProperty.of("pushing");
    protected static final BooleanProperty WITH_FUEL = BooleanProperty.of("with_fuel");

    public LevitatorBlock() {
        super(FabricBlockSettings.of(Material.WOOD));
        setDefaultState(getDefaultState()
                .with(FACING, Direction.DOWN)
                .with(PUSHING, true)
                .with(WITH_FUEL, false)
                .with(POWER, 0)
                .with(POWERED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        boolean pushing = state.get(PUSHING);
        world.setBlockState(pos, state.with(PUSHING, !pushing));
        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PUSHING, WITH_FUEL, POWER, POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getPlayerLookDirection();
        int power = ctx.getWorld().getReceivedRedstonePower(ctx.getBlockPos());
        return getDefaultState().with(POWER, power).with(FACING, facing).with(POWERED, power > 0);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        int oldPower = state.get(POWER);
        int power = world.getReceivedRedstonePower(pos);
        if (oldPower == power) return;

        if (oldPower == 0 || power == 0) world.setBlockState(pos, state.with(POWERED, power > 0).with(POWER, power));
        else world.setBlockState(pos, state.with(POWER, power));

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != LEVITATOR_BLOCK_ENTITY) return null;

        return world.isClient ? LevitatorBlockEntity::clientTicker : LevitatorBlockEntity::serverTicker;
    }

    @Override
    public String getBlockId() {
        return "levitator";
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LevitatorBlockEntity(pos, state);
    }
}
