package ru.feytox.etherology.network.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractS2CPacket implements EtherPacket {

    public void sendForTrackingAndSelf(ServerPlayerEntity relatedPlayer) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(relatedPlayer)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            PacketByteBuf buf = encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, getPacketID(), buf);
        }
        PacketByteBuf buf = encode(PacketByteBufs.create());
        ServerPlayNetworking.send(relatedPlayer, getPacketID(), buf);
    }

    public void sendForTracking(BlockEntity blockEntity) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            PacketByteBuf buf = encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, getPacketID(), buf);
        }
    }

    public void sendForTracking(BlockEntity blockEntity, int exceptId) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, getPacketID(), buf);
        }
    }

    public void sendForTracking(ServerWorld world, BlockPos pos, int exceptId) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, getPacketID(), buf);
        }
    }

    public void sendForTrackingAndSelf(ServerWorld world, BlockPos pos, ServerPlayerEntity relatedPlayer) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            PacketByteBuf buf = encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, getPacketID(), buf);
        }
        PacketByteBuf buf = encode(PacketByteBufs.create());
        ServerPlayNetworking.send(relatedPlayer, getPacketID(), buf);
    }

    @FunctionalInterface
    public interface S2CHandler {
        void receive(S2CPacketInfo packetInfo);
    }
}
