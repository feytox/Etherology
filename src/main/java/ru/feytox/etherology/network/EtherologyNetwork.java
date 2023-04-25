package ru.feytox.etherology.network;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import ru.feytox.etherology.network.animation.AnimationPacketManager;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

import java.util.List;

public class EtherologyNetwork {
    // TODO: 25/04/2023 перенести сюда все остальные пакеты

    public static void registerPackets() {
        registerPackets(new AnimationPacketManager());
    }

    private static void registerPackets(AbstractPacketManager... packetManagers) {
        var s2cBuilder = new ImmutableList.Builder<AbstractS2CPacket>();
        var c2sBuilder = new ImmutableList.Builder<AbstractC2SPacket>();
        for (AbstractPacketManager packetManager : packetManagers) {
            packetManager.registerS2C(s2cBuilder);
            packetManager.registerC2S(c2sBuilder);
        }
        List<AbstractS2CPacket> s2cPackets = s2cBuilder.build();
        List<AbstractC2SPacket> c2sPackets = c2sBuilder.build();

        for (AbstractS2CPacket serverPacket : s2cPackets) {
            ClientPlayNetworking.registerGlobalReceiver(serverPacket.getPacketID(), serverPacket::receive);
        }
        for (AbstractC2SPacket clientPacket : c2sPackets) {
            ServerPlayNetworking.registerGlobalReceiver(clientPacket.getPacketID(), clientPacket::receive);
        }
    }

    public static void sendForTrackingAndSelf(ServerWorld world, AbstractS2CPacket packet, ServerPlayerEntity relatedPlayer) {
        PacketByteBuf buf = packet.encode();
        for (ServerPlayerEntity player : PlayerLookup.tracking(relatedPlayer)) {
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
        ServerPlayNetworking.send(relatedPlayer, packet.getPacketID(), buf);
    }
}
