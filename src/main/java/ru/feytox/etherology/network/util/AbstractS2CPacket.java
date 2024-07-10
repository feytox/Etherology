package ru.feytox.etherology.network.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface AbstractS2CPacket extends CustomPayload {

    default void sendForTrackingAndSelf(ServerPlayerEntity relatedPlayer) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(relatedPlayer)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            ServerPlayNetworking.send(player, this);
        }
        ServerPlayNetworking.send(relatedPlayer, this);
    }

    default void sendForTracking(BlockEntity blockEntity) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            ServerPlayNetworking.send(player, this);
        }
    }

    default void sendForTracking(BlockEntity blockEntity, int exceptId) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            if (player.getId() == exceptId) continue;
            ServerPlayNetworking.send(player, this);
        }
    }

    default void sendForTracking(ServerWorld world, BlockPos pos, int exceptId) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getId() == exceptId) continue;
            ServerPlayNetworking.send(player, this);
        }
    }

    default void sendForTrackingAndSelf(ServerWorld world, BlockPos pos, ServerPlayerEntity relatedPlayer) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            ServerPlayNetworking.send(player, this);
        }
        ServerPlayNetworking.send(relatedPlayer, this);
    }
}
