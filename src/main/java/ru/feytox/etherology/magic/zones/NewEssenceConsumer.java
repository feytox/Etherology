package ru.feytox.etherology.magic.zones;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.components.ZoneComponent;

import java.util.Optional;

public interface NewEssenceConsumer {
    float getConsumingValue();
    int getRadius();
    void increment(float value);

    default Optional<EssenceZoneType> tickConsuming(ServerWorld world, BlockPos pos, EssenceZoneType blockType) {
        ZoneComponent zoneComponent = ZoneComponent.getZone(world.getChunk(pos));
        if (zoneComponent == null || zoneComponent.isEmpty()) return Optional.empty();
        if (blockType.isZone() && !zoneComponent.getZoneType().equals(blockType)) return Optional.empty();

        Integer zoneY = zoneComponent.getZoneY();
        if (zoneY == null) return Optional.empty();
        if (MathHelper.abs(pos.getY() - zoneY) > getRadius()) return Optional.empty();

        float consumedPoints = zoneComponent.decrement(getConsumingValue());
        increment(consumedPoints);
        return Optional.of(zoneComponent.getZoneType());
    }
}
