package ru.feytox.etherology.network.util;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public abstract class AbstractPacketManager {
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {}
    public void registerC2S(ImmutableMap.Builder<Identifier, ServerPlayNetworking.PlayChannelHandler> builder) {}
}
