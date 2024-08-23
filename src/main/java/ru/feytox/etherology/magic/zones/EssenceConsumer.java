package ru.feytox.etherology.magic.zones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.particle.effects.ZoneParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

import java.util.Optional;

public interface EssenceConsumer {

    int getRadius();
    float getConsumingValue();
    void incrementEssence(float value);
    Optional<EssenceSupplier> getCachedZone();
    void setCachedZone(EssenceSupplier zoneCore);

    default Optional<EssenceSupplier> getAndCacheZone(World world, BlockPos pos, EssenceZoneType blockType) {
        if (getCachedZone().isPresent()) return getCachedZone();

        int consumerRadius = getRadius();
        Optional<EssenceSupplier> zoneOptional = BlockPos.findClosest(pos, consumerRadius, consumerRadius, blockPos -> {
                    if (!(world.getBlockEntity(blockPos) instanceof EssenceSupplier essenceSupplier)) return false;
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

    default void tickZoneParticles(ClientWorld world, BlockPos pos, EssenceZoneType blockType) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!OculusItem.isUsing(player)) return;

        EssenceSupplier zone = getAndCacheZone(world, pos, blockType).orElse(null);
        if (zone == null) return;

        float zonePercent = zone.getPoints() / 64.0f;
        int count = MathHelper.ceil(5 * zonePercent);
        ZoneParticleEffect effect = new ZoneParticleEffect(EtherParticleTypes.ZONE_PARTICLE, zone.getZoneType(), pos.toCenterPos());
        Random random = world.getRandom();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (random.nextFloat() > zonePercent * 1 / 200f) continue;
                    effect.spawnParticles(world, count, 0.5, pos.add(dx, dy, dz).toCenterPos());
                }
            }
        }
    }
}
