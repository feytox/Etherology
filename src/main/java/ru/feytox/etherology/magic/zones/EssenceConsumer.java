package ru.feytox.etherology.magic.zones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.particle.effects.ZoneParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

import java.util.Optional;

public interface EssenceConsumer {
    float getConsumingValue();
    void increment(float value);

    default Optional<EssenceZoneType> tickConsuming(ServerWorld world, BlockPos pos, EssenceZoneType blockType) {
        ZoneComponent zoneComponent = getZone(world, pos, blockType);
        if (zoneComponent == null) return Optional.empty();

        float consumedPoints = zoneComponent.decrement(getConsumingValue());
        increment(consumedPoints);
        return Optional.of(zoneComponent.getZoneType());
    }

    default void tickZoneParticles(ClientWorld world, BlockPos pos, EssenceZoneType blockType) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!OculusItem.isUsing(player)) return;

        ZoneComponent zoneComponent = getZone(world, pos, blockType);
        if (zoneComponent == null) return;
        EssenceZone zone = zoneComponent.getEssenceZone();
        if (zone == null) return;

        float zonePercent = zone.getValue() / 64.0f;
        int count = MathHelper.ceil(5 * zonePercent);
        ZoneParticleEffect effect = new ZoneParticleEffect(EtherParticleTypes.ZONE_PARTICLE, zoneComponent.getZoneType(), pos.toCenterPos());
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

    @Nullable
    default ZoneComponent getZone(World world, BlockPos pos, EssenceZoneType blockType) {
        ZoneComponent zoneComponent = ZoneComponent.getZone(world.getChunk(pos));
        if (zoneComponent == null || zoneComponent.isEmpty()) return null;
        if (blockType.isZone() && !zoneComponent.getZoneType().equals(blockType)) return null;
        return zoneComponent;
    }
}
