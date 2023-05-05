package ru.feytox.etherology.util.feyapi;

import net.minecraft.client.world.ClientWorld;

@Deprecated
public interface PacketUpdatable {
    void onPacketUpdate(ClientWorld world);
}
