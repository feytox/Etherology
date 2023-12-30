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
import ru.feytox.etherology.network.interaction.InteractionPacketManager;
import ru.feytox.etherology.network.util.*;

public class EtherologyNetwork {
    // TODO: 25/04/2023 перенести сюда все остальные пакеты

    public static void registerPackets() {
        registerPackets(AnimationPacketManager.INSTANCE, InteractionPacketManager.INSTANCE);
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

    // TODO: 30.12.2023 replace to this.send...

    @Deprecated
    public static void sendForTracking(AbstractS2CPacket packet, BlockEntity blockEntity) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    @Deprecated
    public static void sendForTracking(AbstractS2CPacket packet, BlockEntity blockEntity, int exceptId) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    @Deprecated
    public static void sendForTracking(ServerWorld world, BlockPos pos, int exceptId, AbstractS2CPacket packet) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    @Deprecated
    public static void sendToServer(AbstractC2SPacket packet) {
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        ClientPlayNetworking.send(packet.getPacketID(), buf);
    }
}
