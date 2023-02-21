package name.uwu.feytox.etherology.magic.zones;

import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Optional;

import static name.uwu.feytox.etherology.BlocksRegistry.ZONE_BLOCK_ENTITY;

public interface EssenceConsumer {
    float getConsumingValue();
    float getRadius();
    BlockPos getConsumerPos();
    @Nullable
    BlockPos getCachedZonePos();
    void setCachedCorePos(BlockPos blockPos);
    EssenceZones getZoneType();
    void setZoneType(EssenceZones zoneType);
    void increment(float value);

    @Nullable
    default ZoneBlockEntity getZoneCore(ServerWorld world) {
        BlockPos zonePos = getCachedZonePos();
        if (zonePos != null) {
            Optional<ZoneBlockEntity> match = world.getBlockEntity(zonePos, ZONE_BLOCK_ENTITY);
            if (match.isPresent()) return match.get();
        }

        BlockPos consumerPos = getConsumerPos();
        float radius = getRadius();

        BlockPos minPos = consumerPos.add(-radius, 0, -radius);
        BlockPos maxPos = consumerPos.add(radius, radius, radius);
        EssenceZones zoneType = getZoneType();
        // TODO: 21/02/2023 проверить, нужно ли доп. условие
        boolean is_empty = zoneType.equals(EssenceZones.NULL);

        for (BlockPos blockPos : BlockPos.iterate(minPos, maxPos)) {
            Optional<ZoneBlockEntity> match = world.getBlockEntity(blockPos, ZONE_BLOCK_ENTITY);
            if (match.isPresent() && (is_empty || match.get().getZoneType().equals(zoneType))) {
                setCachedCorePos(blockPos);
                setZoneType(match.get().getZoneType());
                return match.get();
            }
        }

        return null;
    }

    default boolean tickConsume(ServerWorld world) {
        ZoneBlockEntity zoneBlock = getZoneCore(world);
        if (zoneBlock == null) return false;

        float consumedPoints = zoneBlock.decrement(world, getConsumingValue());
        increment(consumedPoints);

        if (consumedPoints == 0) {
            setEmpty();
        }

        return true;
    }

    default void setEmpty() {
        setZoneType(EssenceZones.NULL);
    }

    default boolean isEmpty() {
        return getZoneType().equals(EssenceZones.NULL);
    }
}
