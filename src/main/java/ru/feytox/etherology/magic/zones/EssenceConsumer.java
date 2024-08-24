package ru.feytox.etherology.magic.zones;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public interface EssenceConsumer {

    int getRadius();
    float getConsumingValue();
    void incrementEssence(float value);
    Optional<EssenceSupplier> getCachedZone();
    void setCachedZone(EssenceSupplier zoneCore);

    default Optional<EssenceSupplier> getAndCacheZone(World world, BlockPos pos, EssenceZoneType blockType) {
        if (getCachedZone().filter(EssenceSupplier::isAlive).isPresent()) return getCachedZone();

        int consumerRadius = getRadius();
        Optional<EssenceSupplier> zoneOptional = BlockPos.findClosest(pos, consumerRadius, consumerRadius, blockPos -> {
                    if (!(world.getBlockEntity(blockPos) instanceof EssenceSupplier essenceSupplier)) return false;
                    if (!essenceSupplier.isAlive()) return false;
                    return !blockType.isZone() || essenceSupplier.getZoneType().equals(blockType);
                })
                .map(blockPos -> (EssenceSupplier) world.getBlockEntity(blockPos))
                .filter(essenceSupplier -> essenceSupplier.getPos().isWithinDistance(pos, essenceSupplier.getRadius()));

        zoneOptional.ifPresent(this::setCachedZone);
        return zoneOptional;
    }

    default Optional<EssenceZoneType> tickConsuming(ServerWorld world, BlockPos pos, EssenceZoneType blockType) {
        EssenceSupplier zone = getAndCacheZone(world, pos, blockType).orElse(null);
        if (zone == null) return Optional.empty();

        EssenceZoneType zoneType = zone.getZoneType();
        float consumedPoints = zone.decrement(world, getConsumingValue());
        incrementEssence(consumedPoints);
        return Optional.of(zoneType);
    }
}
