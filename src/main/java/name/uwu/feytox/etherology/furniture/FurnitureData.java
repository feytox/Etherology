package name.uwu.feytox.etherology.furniture;

import name.uwu.feytox.etherology.util.nbt.Nbtable;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public abstract class FurnitureData implements Nbtable {
    public final boolean isBottom;

    public FurnitureData(boolean isBottom) {
        this.isBottom = isBottom;
    }

    public void serverTick(ServerWorld world) {}

    public void clientTick(ClientWorld world) {}

    public void serverUse(ServerWorld world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand) {}

    public void clientUse(ClientWorld world, BlockState state, BlockPos pos, PlayerEntity player, Hand hand) {}
}
