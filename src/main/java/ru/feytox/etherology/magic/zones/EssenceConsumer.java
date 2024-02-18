package ru.feytox.etherology.magic.zones;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface EssenceConsumer {
    float getConsumingValue();
    void increment(float value);

    default Optional<EssenceZoneType> tickConsuming(ServerWorld world, BlockPos pos, EssenceZoneType blockType) {
        ZoneComponent zoneComponent = ZoneComponent.getZone(world.getChunk(pos));
        if (zoneComponent == null || zoneComponent.isEmpty()) return Optional.empty();
        if (blockType.isZone() && !zoneComponent.getZoneType().equals(blockType)) return Optional.empty();

        float consumedPoints = zoneComponent.decrement(getConsumingValue());
        increment(consumedPoints);
        return Optional.of(zoneComponent.getZoneType());
    }
}
