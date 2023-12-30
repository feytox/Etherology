package ru.feytox.etherology.network.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public abstract class AbstractC2SPacket implements EtherPacket {

    public void sendToServer() {
        PacketByteBuf buf = encode(PacketByteBufs.create());
        ClientPlayNetworking.send(getPacketID(), buf);
    }

    @FunctionalInterface
    public interface C2SHandler {
        void receive(C2SPacketInfo packetInfo);
    }
}
