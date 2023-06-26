package ru.feytox.etherology.particle;

import lombok.NonNull;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.particle.types.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.VerticalParticle;

import static ru.feytox.etherology.registry.particle.ServerParticleTypes.THUNDER_ZAP;

public class PealWaveParticle extends VerticalParticle<MovingParticleEffect> {
    public PealWaveParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, new Vec3d(x, y, z), parameters.getMoveVec(), 0.5, parameters, spriteProvider);
        maxAge = 10;
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge(spriteProvider);
    }

    public static void spawnWave(ClientWorld world, @NonNull Vec3d start, @NonNull Vec3d end) {
        MovingParticleEffect effect = new MovingParticleEffect(THUNDER_ZAP, end);
        world.addImportantParticle(effect, start.x, start.y, start.z, 0, 0, 0);
        spawnElectricity(world, start);
        spawnElectricity(world, end);
    }

    private static void spawnElectricity(ClientWorld world, Vec3d entityCenter) {
        Random random = world.getRandom();
        for (int i = 0; i < random.nextBetween(3, 6); i++) {
            DefaultParticleType electricityType = ElectricityParticle.getParticleType(random);
            double ex = entityCenter.x + random.nextDouble() * 0.5;
            double ey = entityCenter.y + random.nextDouble() * 0.5;
            double ez = entityCenter.z + random.nextDouble() * 0.5;
            world.addParticle(electricityType, true, ex, ey, ez, 0.5, 0, 10);
        }
    }
}
