package ru.feytox.etherology.util.feyapi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class EtherNetwork {
    public static final Identifier BLOCK_UPDATE_PACKET_ID = new EIdentifier("block_update_packet");

    @Environment(EnvType.CLIENT)
    public static void registerPackets() {
        registerS2CPackets();
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BLOCK_UPDATE_PACKET_ID, EtherNetwork::blockUpdateHandler);
    }

    public static <T extends BlockEntity> void sendBlockUpdate(ServerWorld world, T blockEntity) {
        BlockPos pos = blockEntity.getPos();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);

        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, 25)) {
            ServerPlayNetworking.send(player, BLOCK_UPDATE_PACKET_ID, buf);
        }
    }

    private static void blockUpdateHandler(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos blockPos = buf.readBlockPos();

        if (client.world == null) return;
        BlockEntity be = client.world.getBlockEntity(blockPos);

        if (be instanceof PacketUpdatable blockEntity) {
            blockEntity.onPacketUpdate(client.world);
        }
    }
}
