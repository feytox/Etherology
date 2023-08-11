package ru.feytox.etherology.block.furniture;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import ru.feytox.etherology.util.feyapi.Nbtable;

public abstract class FurnitureData implements Nbtable {
    public final boolean isBottom;

    public FurnitureData(boolean isBottom) {
        this.isBottom = isBottom;
    }

    public static void updateData(ServerWorld world, BlockPos pos) {
        world.getChunkManager().markForUpdate(pos);
    }

    public void serverUse(ServerWorld world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {}

    public void clientUse(ClientWorld world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {}

    public void render(BlockEntityRendererFactory.Context ctx, FurSlabBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {}
}
