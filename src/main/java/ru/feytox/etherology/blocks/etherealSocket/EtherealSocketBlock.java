package ru.feytox.etherology.blocks.etherealSocket;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.registry.RegistrableBlock;

import static ru.feytox.etherology.BlocksRegistry.ETHEREAL_SOCKET_BLOCK_ENTITY;

public class EtherealSocketBlock extends FacingBlock implements RegistrableBlock, BlockEntityProvider {
    protected static final BooleanProperty WITH_GLINT = BooleanProperty.of("with_glint");

    public EtherealSocketBlock() {
        super(FabricBlockSettings.of(Material.METAL).nonOpaque());
        setDefaultState(getDefaultState()
                .with(FACING, Direction.DOWN)
                .with(WITH_GLINT, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof EtherealSocketBlockEntity socket) {
            return socket.onUse((ServerWorld) world, player, state);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WITH_GLINT);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        Direction facing = side.equals(Direction.DOWN) || side.equals(Direction.UP) ? side.getOpposite() : side;
        return getDefaultState().with(FACING, facing);
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "ethereal_socket";
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ETHEREAL_SOCKET_BLOCK_ENTITY) return null;

        return world.isClient ? EtherealSocketBlockEntity::clientTicker : EtherealSocketBlockEntity::serverTicker;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealSocketBlockEntity(pos, state);
    }
}
