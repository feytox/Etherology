package ru.feytox.etherology.particle;

import lombok.NonNull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.particle.utility.VerticalParticle;

public class PealWaveParticle extends VerticalParticle {
    private final SpriteProvider spriteProvider;

    protected PealWaveParticle(ClientWorld clientWorld, @NonNull Vec3d startCenter, @NonNull Vec3d endCenter, SpriteProvider spriteProvider) {
        super(clientWorld, startCenter, endCenter, 0.5);
        maxAge = 10;

        this.spriteProvider = spriteProvider;
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
        setSpriteForAge(spriteProvider);
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static void spawnWave(ClientWorld world, @NonNull Vec3d start, @NonNull Vec3d end) {
        world.addParticle(Etherology.THUNDER_ZAP, true, start.x, start.y, start.z, end.x, end.y, end.z);
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

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new PealWaveParticle(world, new Vec3d(x, y, z), new Vec3d(velocityX, velocityY, velocityZ), spriteProvider);
        }
    }
}
