package ru.feytox.etherology.network.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface EtherPacket {
    PacketByteBuf encode();
    Identifier getPacketID();
}
