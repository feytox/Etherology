package ru.feytox.etherology.network;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.animation.AnimationPacketManager;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

public class EtherologyNetwork {
    // TODO: 25/04/2023 перенести сюда все остальные пакеты

    public static void registerPackets() {
        registerPackets(new AnimationPacketManager());
    }

    private static void registerPackets(AbstractPacketManager... packetManagers) {
        var s2cBuilder = new ImmutableMap.Builder<Identifier, ClientPlayNetworking.PlayChannelHandler>();
        var c2sBuilder = new ImmutableMap.Builder<Identifier, ServerPlayNetworking.PlayChannelHandler>();
        for (AbstractPacketManager packetManager : packetManagers) {
            packetManager.registerS2C(s2cBuilder);
            packetManager.registerC2S(c2sBuilder);
        }
        ImmutableMap<Identifier, ClientPlayNetworking.PlayChannelHandler> s2cPackets = s2cBuilder.build();
        ImmutableMap<Identifier, ServerPlayNetworking.PlayChannelHandler> c2sPackets = c2sBuilder.build();

        s2cPackets.forEach(ClientPlayNetworking::registerGlobalReceiver);
        c2sPackets.forEach(ServerPlayNetworking::registerGlobalReceiver);
    }

    public static void sendForTrackingAndSelf(AbstractS2CPacket packet, ServerPlayerEntity relatedPlayer) {
        PacketByteBuf buf = packet.encode();
        for (ServerPlayerEntity player : PlayerLookup.tracking(relatedPlayer)) {
            if (player.getUuid().equals(relatedPlayer.getUuid())) continue;
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
        ServerPlayNetworking.send(relatedPlayer, packet.getPacketID(), buf);
    }
}
