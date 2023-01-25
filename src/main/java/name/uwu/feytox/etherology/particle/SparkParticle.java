package name.uwu.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SparkParticle extends MovingParticle {
    protected final SpriteProvider spriteProvider;
    private final int startRed;
    private final int startGreen;
    private final int startBlue;

    public SparkParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        this.spriteProvider = spriteProvider;
        this.age = random.nextBetween(0, 70);
        this.setSpriteForAge(spriteProvider);
//        this.setRGB(255, 215, 0);
        this.startRed = MathHelper.floor(this.red * 255);
        this.startGreen = MathHelper.floor(this.green * 255);
        this.startBlue = MathHelper.floor(this.blue * 255);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteForAge(spriteProvider);

        Vec3d vec = new Vec3d(endX-x, endY-y, endZ-z);
        double vecLength = vec.length();
        double fullPath = new Vec3d(endX-startX, endY-startY, endZ-startZ).length();

        this.setRGB(startRed + (83 - startRed) * ((fullPath - vecLength) / fullPath),
                startGreen + (14 - startGreen) * ((fullPath - vecLength) / fullPath),
                startBlue + (255 - startBlue) * ((fullPath - vecLength) / fullPath));
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
            return new SparkParticle(world, x, y, z, velocityX, velocityY, velocityZ,
                    this.spriteProvider);
        }
    }
}
