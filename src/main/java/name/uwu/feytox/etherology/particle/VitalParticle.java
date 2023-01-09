package name.uwu.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class VitalParticle extends MovingParticle {
    private double currentAngle = 0.1 * Math.PI;
    private int direction = -1;
    private Vec3d trueVec;

    public VitalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.trueVec = new Vec3d(endX-startX, endY-startY, endZ-startZ);
        this.setAngle(Random.create().nextBetween(0, 360));
    }

    @Override
    public void tick() {
        super.tick();
        this.updateAngle(1f);
    }

    //    @Override
//    public void tick() {
//        prevPosX = x;
//        prevPosY = y;
//        prevPosZ = z;
//
//        double trueLen = trueVec.length();
//
//        double fullPath = new Vec3d(endX-startX, endY-startY, endZ-startZ).length();
//
//        if (this.age++ >= this.maxAge) {
//            this.markDead();
//        } else {
//            double f = (fullPath - trueLen) / fullPath;
//            double deltaC = 0.4 * Math.pow(f+0.5, 3);
//
//
//            if (trueLen <= 0.5f) {
//                this.markDead();
//                return;
//            }
//
//            Vec3d deltaVec = trueVec.multiply(Math.min(deltaC / trueLen, 1))
//                    .rotateY((float) currentAngle);
//            x += deltaVec.x;
//            y += deltaVec.y;
//            z += deltaVec.z;
//
//            trueVec = trueVec.subtract(trueVec.multiply(Math.min(deltaC / trueLen, 1)));
//
//            currentAngle += direction * 0.1;
//            if (currentAngle <= -0.1 * Math.PI || currentAngle >= 0.1 * Math.PI) {
//                direction *= -1;
//            }
//        }
//    }

    @Override
    protected int getBrightness(float tint) {
        float f = ((float)this.age + tint) / (float)this.maxAge;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        int i = super.getBrightness(tint);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
            VitalParticle particle = new VitalParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
