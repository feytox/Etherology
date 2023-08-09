package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.SparkParticle;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

import java.util.ArrayList;
import java.util.List;

public class SedimentarySparkInfo extends ParticleInfo<SparkParticle, SparkParticleEffect> {
    private final EssenceZoneType zoneType;
    private final Vec3d endPos;

    public SedimentarySparkInfo(ClientWorld clientWorld, double x, double y, double z, SparkParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        zoneType = parameters.getZoneType();
        endPos = parameters.getMoveVec();
    }

    @Override
    public void extraInit(SparkParticle particle) {
        particle.setSpriteForAge();
        particle.setAge(particle.getRandom().nextBetween(0, 10));
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        RGBColor start = zoneType.getStartColor();
        RGBColor end = zoneType.getEndColor();
        return start == null || end == null ? null : FeyColor.getRandomColor(start, end, random);
    }

    @Override
    public void tick(SparkParticle particle) {
        particle.acceleratedMovingTick(0.01f, 0.1f, false, endPos);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 20;
    }

    public static void spawnSedimentaryParticle(World world, BlockPos blockPos, EssenceZoneType zoneType) {
        if (!zoneType.isZone()) return;

        Vec3d centerPos = blockPos.toCenterPos();
        Vec3d diffPos = new Vec3d(0.8d, 0.8d, 0.8d);
        List<Vec3d> fullPoses = listOf(centerPos.add(diffPos), centerPos.subtract(diffPos), 0.1d);
        List<Vec3d> particlePoses = fullPoses.stream().filter(vec3d ->
                Math.abs(centerPos.y - vec3d.x) >= 0.7 || Math.abs(centerPos.y - vec3d.y) >= 0.7 || Math.abs(centerPos.z - vec3d.z) >= 0.7).toList();

        Random random = world.getRandom();
        particlePoses.forEach(pos -> {
            if (random.nextDouble() > 0.005) return;
            SparkParticleEffect effect = new SparkParticleEffect(ServerParticleTypes.SPARK, pos.add(0, -0.2, 0), zoneType);
            effect.spawnParticles(world, 1, 0, pos);
        });
    }

    private static List<Vec3d> listOf(Vec3d startPos, Vec3d endPos, double step) {
        List<Vec3d> result = new ArrayList<>();

        double minX = Math.min(startPos.x, endPos.x);
        double maxX = Math.max(startPos.x, endPos.x);
        double minY = Math.min(startPos.y, endPos.y);
        double maxY = Math.max(startPos.y, endPos.y);
        double minZ = Math.min(startPos.z, endPos.z);
        double maxZ = Math.max(startPos.z, endPos.z);

        for (double x = minX; x <= maxX; x += step) {
            for (double y = minY; y <= maxY; y += step) {
                for (double z = minZ; z <= maxZ; z += step) {
                    result.add(new Vec3d(x, y, z));
                }
            }
        }
        return result;
    }
}
