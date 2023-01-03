package name.uwu.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class MovingParticle extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;
    private final double endX;
    private final double endY;
    private final double endZ;
    private final SpriteProvider spriteProvider;

    public MovingParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                          SpriteProvider spriteProvider) {
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
        this.spriteProvider = spriteProvider;
//        float scale = (float) Math.min(Math.random() + 0.5f, 1);
//        this.scale(scale);
//        this.setSpriteForAge(spriteProvider);
        this.setSprite(spriteProvider);
    }

    @Override
    public void tick() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;

        Vec3d vec = new Vec3d(endX-x, endY-y, endZ-z);
        double vecLength = vec.length();

        double fullPath = new Vec3d(endX-startX, endY-startY, endZ-startZ).length();
//        age = MathHelper.ceil(maxAge * (fullPath - vec.length()) / fullPath);

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
//            double f = (double) age / maxAge;
            double f = (fullPath - vec.length()) / fullPath;
            double deltaC = f*f * 0.5 + 0.06;

            if (vecLength <= 0.5f) {
                this.markDead();
                return;
            }

            Vec3d deltaVec = vec.multiply(deltaC / vecLength);
            x += deltaVec.x;
            y += deltaVec.y;
            z += deltaVec.z;
        }
//        this.setSpriteForAge(this.spriteProvider);
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
            return new MovingParticle(world, x, y, z, velocityX, velocityY, velocityZ,
                    this.spriteProvider);
        }
    }
}
