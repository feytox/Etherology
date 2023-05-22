package ru.feytox.etherology.network;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.animation.AnimationPacketManager;
import ru.feytox.etherology.network.util.*;

public class EtherologyNetwork {
    // TODO: 25/04/2023 перенести сюда все остальные пакеты

    public static void registerPackets() {
        registerPackets(new AnimationPacketManager());
    }

    private static void registerPackets(AbstractPacketManager... packetManagers) {
        var s2cBuilder = new ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler>();
        var c2sBuilder = new ImmutableMap.Builder<Identifier, AbstractC2SPacket.C2SHandler>();
        for (AbstractPacketManager packetManager : packetManagers) {
            packetManager.registerS2C(s2cBuilder);
            packetManager.registerC2S(c2sBuilder);
        }
        ImmutableMap<Identifier, AbstractS2CPacket.S2CHandler> s2cPackets = s2cBuilder.build();
        ImmutableMap<Identifier, AbstractC2SPacket.C2SHandler> c2sPackets = c2sBuilder.build();

        s2cPackets.forEach((id, handler) ->
                ClientPlayNetworking.registerGlobalReceiver(id, (client, handler1, buf, responseSender) ->
                        handler.receive(new S2CPacketInfo(client, handler1, buf, responseSender))));
        c2sPackets.forEach((id, handler) ->
                ServerPlayNetworking.registerGlobalReceiver(id, ((server, player, handler1, buf, responseSender) ->
                        handler.receive(new C2SPacketInfo(server, player, handler1, buf, responseSender)))));
    }

    public static void sendForTrackingAndSelf(AbstractS2CPacket packet, ServerPlayerEntity relatedPlayer) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(relatedPlayer)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        ServerPlayNetworking.send(relatedPlayer, packet.getPacketID(), buf);
    }

    public static void sendForTracking(AbstractS2CPacket packet, BlockEntity blockEntity) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    public static void sendForTracking(ServerWorld world, BlockPos pos, int exceptId, AbstractS2CPacket packet) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            // TODO: 22/05/2023 проверить, точно ли нужно каждый раз создавать buf
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    public static void sendForTrackingAndSelf(ServerWorld world, BlockPos pos, ServerPlayerEntity relatedPlayer, AbstractS2CPacket packet) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        ServerPlayNetworking.send(relatedPlayer, packet.getPacketID(), buf);
    }

    public static void sendToServer(AbstractC2SPacket packet) {
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        ClientPlayNetworking.send(packet.getPacketID(), buf);
    }
}
