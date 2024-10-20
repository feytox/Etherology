package ru.feytox.etherology.magic.seal;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface EssenceSupplier {

    int getRadius();
    BlockPos getSupplierPos();
    SealType getSealType();
    float getFillPercent();
    float decrement(ServerWorld world, float value);
    boolean isAlive();
}
