package ru.feytox.etherology.magic.zones;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface EssenceSupplier {

    int getRadius();
    BlockPos getSupplierPos();
    EssenceZoneType getZoneType();
    float getFillPercent();
    float decrement(ServerWorld world, float value);
    boolean isAlive();
}
