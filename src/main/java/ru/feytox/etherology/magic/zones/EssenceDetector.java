package ru.feytox.etherology.magic.zones;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.block.zone.ZoneCoreBlockEntity;

import java.util.Optional;

public interface EssenceDetector extends EssenceConsumer {

    default Optional<EssenceSupplier> getAndCacheZone(World world, BlockPos pos) {
        return getAndCacheZone(world, pos, EssenceZoneType.EMPTY);
    }

    @Override
    default int getRadius() {
        return ZoneCoreBlockEntity.MAX_RADIUS;
    }

    @Override
    default float getConsumingValue() {
        return 0.0f;
    }

    @Override
    default void incrementEssence(float value) {}

    @Override
    default Optional<EssenceZoneType> tickConsuming(ServerWorld world, BlockPos pos, EssenceZoneType blockType) {
        return Optional.empty();
    }
}
