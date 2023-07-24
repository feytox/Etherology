package ru.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

// TODO: 24.07.2023 replace with new
@Deprecated
public class OldSparkParticle extends OldMovingParticle {
    private static SpriteProvider spriteProvider;
    private final int startRed;
    private final int startGreen;
    private final int startBlue;

    public OldSparkParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.maxAge = 80;
        this.age = random.nextBetween(0, 70);
        this.startRed = 244;
        this.startGreen = 194;
        this.startBlue = 133;
        this.setRGB(startRed, startGreen, startBlue);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        acceleratedMovingTick(0.4f, 0.5f, true, false);
        this.setSpriteForAge(spriteProvider);

        Vec3d vec = new Vec3d(endX-x, endY-y, endZ-z);
        double vecLength = vec.length();
        double fullPath = new Vec3d(endX-startX, endY-startY, endZ-startZ).length();


        int endRed = 83;
        int endGreen = 14;
        int endBlue = 255;
        this.setRGB(startRed + (endRed - startRed) * ((fullPath - vecLength) / fullPath),
                startGreen + (endGreen - startGreen) * ((fullPath - vecLength) / fullPath),
                startBlue + (endBlue - startBlue) * ((fullPath - vecLength) / fullPath));
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        public Factory(SpriteProvider spriteProvider) {
            OldSparkParticle.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new OldSparkParticle(world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
