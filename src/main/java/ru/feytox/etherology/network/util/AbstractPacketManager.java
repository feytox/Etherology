package ru.feytox.etherology.network.util;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public abstract class AbstractPacketManager {
    @Deprecated(forRemoval = true)
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {}
    @Deprecated(forRemoval = true)
    public void registerC2S(ImmutableMap.Builder<Identifier, AbstractC2SPacket.C2SHandler> builder) {}

    public void registerS2C(ClientData<?> data) {}
    public void registerC2S(ServerData<?> data) {}

    @FunctionalInterface
    public interface ClientData<T extends CustomPayload> {
        void apply(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec, ClientPlayNetworking.PlayPayloadHandler<T> handler);
    }

    @FunctionalInterface
    public interface ServerData<T extends CustomPayload> {
        void apply(CustomPayload.Id<T> id, PacketCodec<RegistryByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler);
    }
}
