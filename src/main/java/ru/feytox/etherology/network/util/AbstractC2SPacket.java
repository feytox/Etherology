package ru.feytox.etherology.network.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public interface AbstractC2SPacket extends CustomPayload {

    @RequiredArgsConstructor @Getter
    class PacketType<T extends AbstractC2SPacket> {
        private final Id<T> id;
        private final PacketCodec<RegistryByteBuf, T> codec;
        private final ServerPlayNetworking.PlayPayloadHandler<T> handler;
    }
}
