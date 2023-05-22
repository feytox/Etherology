package ru.feytox.etherology.network.util;

public abstract class AbstractC2SPacket implements EtherPacket {
    @FunctionalInterface
    public interface C2SHandler {
        void receive(C2SPacketInfo packetInfo);
    }
}
