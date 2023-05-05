package ru.feytox.etherology.network;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.animation.AnimationPacketManager;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;

public class EtherologyNetwork {
    // TODO: 25/04/2023 перенести сюда все остальные пакеты

    public static void registerPackets() {
        registerPackets(new AnimationPacketManager());
    }

    private static void registerPackets(AbstractPacketManager... packetManagers) {
        var s2cBuilder = new ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler>();
        var c2sBuilder = new ImmutableMap.Builder<Identifier, ServerPlayNetworking.PlayChannelHandler>();
        for (AbstractPacketManager packetManager : packetManagers) {
            packetManager.registerS2C(s2cBuilder);
            packetManager.registerC2S(c2sBuilder);
        }
        ImmutableMap<Identifier, AbstractS2CPacket.S2CHandler> s2cPackets = s2cBuilder.build();
        ImmutableMap<Identifier, ServerPlayNetworking.PlayChannelHandler> c2sPackets = c2sBuilder.build();

        s2cPackets.forEach((id, handler) ->
                ClientPlayNetworking.registerGlobalReceiver(id, (client, handler1, buf, responseSender) ->
                        handler.receive(new S2CPacketInfo(client, handler1, buf, responseSender))));
        c2sPackets.forEach(ServerPlayNetworking::registerGlobalReceiver);
    }

    public static void sendForTrackingAndSelf(AbstractS2CPacket packet, ServerPlayerEntity relatedPlayer) {
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        for (ServerPlayerEntity player : PlayerLookup.tracking(relatedPlayer)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
        ServerPlayNetworking.send(relatedPlayer, packet.getPacketID(), buf);
    }

    public static void sendForTracking(AbstractS2CPacket packet, BlockEntity blockEntity) {
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }
}
