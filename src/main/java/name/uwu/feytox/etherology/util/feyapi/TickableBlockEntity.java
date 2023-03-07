package name.uwu.feytox.etherology.util.feyapi;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TickableBlockEntity extends BlockEntity {
    public TickableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {}
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {}

    public static void clientTicker(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof TickableBlockEntity be) {
            be.clientTick((ClientWorld) world, blockPos, state);
        }
    }

    public static void serverTicker(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof TickableBlockEntity be) {
            be.serverTick((ServerWorld) world, blockPos, state);
        }
    }
}
