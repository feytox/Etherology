package ru.feytox.etherology.util.feyapi;

import net.minecraft.client.world.ClientWorld;

public interface PacketUpdatable {
    void onPacketUpdate(ClientWorld world);
}
