package ru.feytox.etherology.enums;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.armillar.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.network.interaction.SmallLightningS2C;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public enum InstabilityType implements StringIdentifiable {
    // TODO: 11.08.2023 change chances
    NULL(0.0f, 0.0f, 0.0f, 0.0f, 0.0f),
    VERY_LOW(0.025f, 0.1f, 0.0f, 0.0f, 0.0f),
    LOW(0.05f, 0.025f, 0.015f, 0.0f, 0.0f),
    MEDIUM(0.05f, 0.05f, 0.03f, 0.01f, 0.0f),
    HIGH(0.075f, 0.1f, 0.05f, 0.025f, 0.005f),
    VERY_HIGH(0.1f, 0.125f, 0.1f, 0.05f, 0.025f);

    // выброс искажения в мир
    private final float corruptionChance;
    // микро-молнии
    private final float lightningChance;
    // выпад предмета
    private final float dropChance;
    // микро-взрывы
    private final float boomChance;
    // удаление предмета
    private final float removeChance;

    public void corruptionEvent(ServerWorld world, float instability, BlockPos pos) {
        if (shouldSkip(world, instability, corruptionChance)) return;

        Corruption corruption = new Corruption(instability);
        corruption.placeInChunk(world, pos);
    }

    public void lightningEvent(ServerWorld world, ArmillaryMatrixBlockEntity matrix, ArmillaryState matrixState, float instability) {
        if (shouldSkip(world, instability, lightningChance)) return;

        Random random = world.getRandom();
        Vec3d centerPos = matrix.getCenterPos(matrixState);
        Optional<? extends LivingEntity> entityMatch = matrix.findClosestEntity(world, centerPos);

        Vec3d endPos = matrix.getPos().toCenterPos().add(0, -0.5, 0);
        if (entityMatch.isPresent()) endPos = entityMatch.get().getPos();
        Vec3d randomPos = endPos.add(random.nextBetween(-4, 4), 0, random.nextBetween(-4, 4));

        SmallLightningS2C.sendForTracking(world, matrix, centerPos, randomPos, random.nextBetween(5, 7), instability);
    }

    public void dropEvent(ServerWorld world, ArmillaryMatrixBlockEntity matrix, float instability) {
        if (shouldSkip(world, instability, dropChance)) return;

        PedestalBlockEntity pedestal = getRandomPedestal(world, matrix);
        if (pedestal == null) return;
        ItemScatterer.spawn(world, pedestal.getPos().add(0, 1, 0), pedestal);
        pedestal.clear();
        pedestal.syncData(world);
    }

    public void boomEvent(ServerWorld world, float instability, BlockPos pos) {
        if (shouldSkip(world, instability, boomChance)) return;

        Random random = world.getRandom();
        world.createExplosion(null, pos.getX() + random.nextBetween(-8, 8), pos.getY() + 0.25, pos.getZ() + random.nextBetween(-8, 8), 1, World.ExplosionSourceType.NONE);
    }

    public void removeEvent(ServerWorld world, ArmillaryMatrixBlockEntity matrix, float instability) {
        if (shouldSkip(world, instability, removeChance)) return;

        PedestalBlockEntity pedestal = getRandomPedestal(world, matrix);
        if (pedestal == null) return;
        pedestal.clear();
        pedestal.syncData(world);
    }

    private static boolean shouldSkip(ServerWorld world, float instability, float chance) {
        float instabilityPercent = Math.min(instability / 33.33333f, 1.0f);
        return world.getRandom().nextFloat() > chance * (instabilityPercent * 0.25 + 1);
    }

    @Nullable
    private static PedestalBlockEntity getRandomPedestal(ServerWorld world, ArmillaryMatrixBlockEntity matrix) {
        Random random = world.getRandom();
        List<PedestalBlockEntity> pedestals = matrix.getCachedPedestals(world);
        if (pedestals.isEmpty()) return null;

        return pedestals.get(random.nextInt(pedestals.size()));
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
