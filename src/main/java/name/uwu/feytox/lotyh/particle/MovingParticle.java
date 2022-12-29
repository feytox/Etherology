package name.uwu.feytox.lotyh.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class MovingParticle extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;
    private final double endX;
    private final double endY;
    private final double endZ;

    public MovingParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        // end coords = velocity
        super(clientWorld, d, e, f, g, h, i);
        this.x = d;
        this.y = e;
        this.z = f;
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;
        this.endX = g;
        this.endY = h;
        this.endZ = i;
        this.maxAge = 80;
        this.scale(0.25f);
    }

    @Override
    public void tick() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        if (endX == x && endY == y && endZ == z) {
            markDead();
        } else {
            double f = (double) age / maxAge;
            f = f * f;
            double deltaC = 1.3 / 20f;
            x = changeCoord(x, deltaC, endX);
            y = changeCoord(y, deltaC, endY);
            z = changeCoord(z, deltaC, endZ);
        }
    }

    public static double changeCoord(double c, double deltaC, double endC) {
        if (c <= endC) {
            return Math.min(endC, c + deltaC);
        }
        return Math.max(endC, c - deltaC);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
            MovingParticle movingParticle = new MovingParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            movingParticle.setSprite(this.spriteProvider);
            return movingParticle;
        }
    }
}
