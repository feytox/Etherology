package name.uwu.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.Etherology.ELECTRICITY1;
import static name.uwu.feytox.etherology.Etherology.ELECTRICITY2;

public class ElectricityParticle extends MovingParticle {
    protected final SpriteProvider spriteProvider;

    public ElectricityParticle(ClientWorld clientWorld, double d, double e, double f, double instability, double isCenter, SpriteProvider spriteProvider) {
        // (normal - 1.0, max 60s = 33.33369)
        super(clientWorld, d, e, f, d, e, f);
        if (isCenter < 25 && isCenter > 15) {
            this.scale(1.4f);
        }
        this.spriteProvider = spriteProvider;
        this.maxAge = 10;
        this.age = random.nextBetween(0, maxAge - 5);
        this.setSpriteForAge(spriteProvider);
        this.setRGB(255 - (104 * instability / 33.33369d), 115, 255);
        this.setAngle(Random.create().nextBetween(0, 360));
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
        this.setSpriteForAge(spriteProvider);
        this.updateAngle(1f);
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            // velocityX = instability
            // velocityY = is_center
            return new ElectricityParticle(world, x, y, z, velocityX, velocityY, this.spriteProvider);
        }
    }

    public static DefaultParticleType getParticleType(Random random) {
        return random.nextDouble() <= 0.5d ? ELECTRICITY1 : ELECTRICITY2;
    }

    /**
     * recommended for single particle
     */
    public static DefaultParticleType getParticleType() {
        return getParticleType(Random.create());
    }
}
