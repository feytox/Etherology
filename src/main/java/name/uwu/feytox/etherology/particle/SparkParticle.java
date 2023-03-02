package name.uwu.feytox.etherology.particle;

import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.util.feyapi.EVec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SparkParticle extends MovingParticle {
    private static SpriteProvider spriteProvider;
    private int startRed;
    private int startGreen;
    private int startBlue;
    private int endRed = 83;
    private int endGreen = 14;
    private int endBlue = 255;
    private boolean isSedimentary = false;

    public SparkParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.maxAge = 80;
        this.age = random.nextBetween(0, 70);
        this.setSpriteForAge(spriteProvider);
//        this.setRGB(255, 215, 0);
        this.startRed = MathHelper.floor(this.red * 255);
        this.startGreen = MathHelper.floor(this.green * 255);
        this.startBlue = MathHelper.floor(this.blue * 255);
    }

    public SparkParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, EssenceZones zoneType) {
        this(clientWorld, d, e, f, g, h, i);
        this.maxAge = 20;
        this.age = random.nextBetween(0, 10);
        this.startRed = zoneType.getFirstColor().r();
        this.startGreen = zoneType.getFirstColor().g();
        this.startBlue = zoneType.getFirstColor().b();
        this.endRed = zoneType.getSecondColor().r();
        this.endGreen = zoneType.getSecondColor().g();
        this.endBlue = zoneType.getSecondColor().b();
        isSedimentary = true;
    }

    @Override
    public void tick() {
        acceleratedMovingTick(isSedimentary ? 0.01f : 0.4f, isSedimentary ? 0.1f : 0.5f, !isSedimentary);
        this.setSpriteForAge(spriteProvider);

        Vec3d vec = new Vec3d(endX-x, endY-y, endZ-z);
        double vecLength = vec.length();
        double fullPath = new Vec3d(endX-startX, endY-startY, endZ-startZ).length();

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
            SparkParticle.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new SparkParticle(world, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    public static void spawnSedimentaryParticle(ClientWorld world, BlockPos blockPos, EssenceZones zoneType) {
        Vec3d centerPos = new Vec3d(blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5);
        Vec3d diffPos = new Vec3d(0.8d, 0.8d, 0.8d);
        List<Vec3d> fullPoses = EVec3d.listOf(centerPos.add(diffPos), centerPos.subtract(diffPos), 0.1d);
        List<Vec3d> particlePoses = fullPoses.stream().filter(vec3d ->
                Math.abs(centerPos.y - vec3d.x) >= 0.7 || Math.abs(centerPos.y - vec3d.y) >= 0.7 || Math.abs(centerPos.z - vec3d.z) >= 0.7).toList();

        Random random = Random.create();
        particlePoses.forEach(vec3d -> {
            if (random.nextDouble() > 0.005) return;
            Vec3d target = vec3d.add(0, -0.2, 0);
            SparkParticle sparkParticle = new SparkParticle(world, vec3d.x, vec3d.y, vec3d.z,
                    target.x, target.y, target.z, zoneType);
            MinecraftClient.getInstance().particleManager.addParticle(sparkParticle);
        });
    }
}
