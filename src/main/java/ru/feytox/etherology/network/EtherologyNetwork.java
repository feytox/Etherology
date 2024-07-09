package ru.feytox.etherology.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EtherologyNetwork {

    public static void registerCommonSide() {
        // animation
        registerS2C(StartBlockAnimS2C.ID, StartBlockAnimS2C.CODEC);
    }

    public static void registerClientSide() {
        // animation
        registerHandlerS2C(StartBlockAnimS2C.ID, StartBlockAnimS2C::receive);
        // stopship: continue rewriting networking
        // good luck
    }

    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }

    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(id, codec);
    }

    private static <T extends CustomPayload> void registerHandlerS2C(CustomPayload.Id<T> id, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
    }

    public static CustomPayload.Id<? extends CustomPayload> id(String id) {
        return new CustomPayload.Id<>(EIdentifier.of(id));
    }

    // TODO: 30.12.2023 replace to this.send...

    @Deprecated(forRemoval = true)
    public static void sendForTracking(AbstractS2CPacket packet, BlockEntity blockEntity) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    @Deprecated(forRemoval = true)
    public static void sendForTracking(AbstractS2CPacket packet, BlockEntity blockEntity, int exceptId) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(blockEntity)) {
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    @Deprecated(forRemoval = true)
    public static void sendForTracking(ServerWorld world, BlockPos pos, int exceptId, AbstractS2CPacket packet) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
            if (player.getId() == exceptId) continue;
            PacketByteBuf buf = packet.encode(PacketByteBufs.create());
            ServerPlayNetworking.send(player, packet.getPacketID(), buf);
        }
    }

    @Deprecated(forRemoval = true)
    public static void sendToServer(AbstractC2SPacket packet) {
        PacketByteBuf buf = packet.encode(PacketByteBufs.create());
        ClientPlayNetworking.send(packet.getPacketID(), buf);
    }
}
