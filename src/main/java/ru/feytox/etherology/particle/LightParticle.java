package ru.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class LightParticle extends MovingParticle {
    private final int startRed;
    private final int startGreen;
    private final int startBlue;
    private final boolean isVital;
    private final boolean isSpark;

    public LightParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                         boolean isVital, boolean isSpark) {
        super(clientWorld, d, e, f, g, h, i);
        this.isVital = isVital;
        this.isSpark = isSpark;

        if (!isVital && !isSpark) this.scale(0.3f);
        else if (!isVital) this.scale(0.1f);
        else {
            Random random = Random.create();
            float randFloat = random.nextFloat();
            this.scale(0.05f + randFloat * 0.1f);
            this.alpha *= 0.78f * randFloat;
        }

        if (!isVital && !isSpark) setRGB(new RGBColor(244, 194, 133));

        this.startRed = MathHelper.floor(this.red * 255);
        this.startGreen = MathHelper.floor(this.green * 255);
        this.startBlue = MathHelper.floor(this.blue * 255);
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Override
    public void tick() {
        if (!isVital && !isSpark) {
            acceleratedMovingTick(0.1f, 0.5f, true);
        }
        if (isVital | isSpark) super.tick();
        if (isSpark) {
            Vec3d vec = new Vec3d(endX-x, endY-y, endZ-z);
            double vecLength = vec.length();
            double fullPath = new Vec3d(endX-startX, endY-startY, endZ-startZ).length();

            this.setRGB(startRed + (83 - startRed) * ((fullPath - vecLength) / fullPath),
                    startGreen + (14 - startGreen) * ((fullPath - vecLength) / fullPath),
                    startBlue + (255 - startBlue) * ((fullPath - vecLength) / fullPath));
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SimpleFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SimpleFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            LightParticle particle = new LightParticle(world, x, y, z, velocityX, velocityY, velocityZ, false, false);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SparkFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SparkFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            LightParticle particle = new LightParticle(world, x, y, z, velocityX, velocityY, velocityZ, false, true);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
