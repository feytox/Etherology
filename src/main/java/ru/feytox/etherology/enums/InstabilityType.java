package ru.feytox.etherology.enums;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.corruption.Corruption;

@RequiredArgsConstructor
public enum InstabilityType implements StringIdentifiable {
    NULL(0.0f, 0.0f, 0.0f, 0.0f, 0.0f),
    VERY_LOW(0.05f, 0.2f, 0.0f, 0.0f, 0.0f),
    LOW(0.1f, 0.05f, 0.03f, 0.0f, 0.0f),
    MEDIUM(0.1f, 0.1f, 0.06f, 0.02f, 0.0f),
    HIGH(0.15f, 0.2f, 0.1f, 0.05f, 0.01f),
    VERY_HIGH(0.2f, 0.25f, 0.2f, 0.1f, 0.05f);

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

    public boolean corruptionEvent(ServerWorld world, float instability, BlockPos pos) {
        if (!checkRandom(world, instability, corruptionChance)) return false;

        Corruption corruption = new Corruption(instability);
        corruption.placeInChunk(world, pos);
        return true;
    }

    public boolean lightningEvent(ServerWorld world, float instability, BlockPos pos) {
        if (!checkRandom(world, instability, lightningChance)) return false;

        // TODO: 07.08.2023 small lightning
        return true;
    }

    public boolean dropEvent(ServerWorld world, float instability, BlockPos pos) {
        if (!checkRandom(world, instability, dropChance)) return false;

        // TODO: 07.08.2023 pedestals getter and item drop
        return true;
    }

    public boolean boomEvent(ServerWorld world, float instability, BlockPos pos) {
        if (!checkRandom(world, instability, boomChance)) return false;

        Random random = world.getRandom();
        world.createExplosion(null, pos.getX() + random.nextBetween(-8, 8), pos.getY(), pos.getZ() + random.nextBetween(-8, 8), 1, World.ExplosionSourceType.BLOCK);
        return true;
    }

    public boolean removeEvent(ServerWorld world, float instability, BlockPos pos) {
        if (!checkRandom(world, instability, removeChance)) return false;

        // TODO: 07.08.2023 pedestals getter and item remove
        return true;
    }

    private static boolean checkRandom(ServerWorld world, float instability, float chance) {
        return world.getRandom().nextFloat() <= instability * chance;
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
