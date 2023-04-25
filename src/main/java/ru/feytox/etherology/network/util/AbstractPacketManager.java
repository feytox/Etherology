package ru.feytox.etherology.network.util;

import com.google.common.collect.ImmutableList;

public abstract class AbstractPacketManager {
    public void registerS2C(ImmutableList.Builder<AbstractS2CPacket> builder) {}
    public void registerC2S(ImmutableList.Builder<AbstractC2SPacket> builder) {}
}
