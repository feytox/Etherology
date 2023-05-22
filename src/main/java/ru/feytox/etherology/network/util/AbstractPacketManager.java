package ru.feytox.etherology.network.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;

public abstract class AbstractPacketManager {
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {}
    public void registerC2S(ImmutableMap.Builder<Identifier, AbstractC2SPacket.C2SHandler> builder) {}
}
