package ru.feytox.etherology.magic.seal;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.block.seal.SealBlockEntity;

import java.util.Optional;

public interface EssenceDetector extends EssenceConsumer {

    default Optional<EssenceSupplier> getAndCacheSeal(World world, BlockPos pos) {
        return getAndCacheSeal(world, pos, SealType.EMPTY);
    }

    @Override
    default int getRadius() {
        return SealBlockEntity.MAX_RADIUS;
    }

    @Override
    default float getConsumingValue() {
        return 0.0f;
    }

    @Override
    default void incrementEssence(float value) {}

    @Override
    default Optional<SealType> tickConsuming(ServerWorld world, BlockPos pos, SealType blockType) {
        return Optional.empty();
    }
}
