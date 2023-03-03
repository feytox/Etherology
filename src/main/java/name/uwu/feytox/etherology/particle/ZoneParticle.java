package name.uwu.feytox.etherology.particle;

import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.util.feyapi.FeyRandom;
import name.uwu.feytox.etherology.util.feyapi.RGBColor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class ZoneParticle extends MovingParticle {
    public static final int PARTICLE_RADIUS = 8;
    private final SpriteProvider spriteProvider;

    protected ZoneParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i,
                           SpriteProvider spriteProvider, EssenceZones zoneType) {
        super(clientWorld, d, e, f, g, h, i);

        if (h < -4000) {
            this.endX = d + FeyRandom.getValueSign(this.random) * this.random.nextDouble() * 3;
            this.endY = e + FeyRandom.getValueSign(this.random) * this.random.nextDouble() * 2;
            this.endZ = f + FeyRandom.getValueSign(this.random) * this.random.nextDouble() * 3;
        }

        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 20 * this.random.nextBetween(1, 2);
        scale(0.15f);

        RGBColor gradientColor = getGradientColor(zoneType.getFirstColor(), zoneType.getSecondColor(), this.random);
        this.setRGB(gradientColor);
    }

    public static RGBColor getGradientColor(RGBColor startColor, RGBColor endColor, Random random) {
        int gradientLength = Math.abs(endColor.r() - startColor.r()) + Math.abs(endColor.g() - startColor.g()) + Math.abs(endColor.b() - startColor.b());
        int randomPos = random.nextInt(gradientLength + 1);
        int r = getColorComponent(startColor.r(), endColor.r(), randomPos);
        int g = getColorComponent(startColor.g(), endColor.g(), randomPos);
        int b = getColorComponent(startColor.b(), endColor.b(), randomPos);
        return new RGBColor(r, g, b);
    }

    public static int getColorComponent(int startValue, int endValue, int pos) {
        if (startValue == endValue) {
            return startValue;
        }
        int delta = endValue - startValue;
        int increment = delta > 0 ? 1 : -1;
        int currentPos = 0;
        int currentValue = startValue;
        while (currentPos < pos && currentValue != endValue) {
            currentPos++;
            currentValue += increment;
        }
        return currentValue;
    }

    @Override
    public void tick() {
        acceleratedMovingTick(0.2f, 0.3f, true);
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class KetaFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public KetaFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ZoneParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, EssenceZones.KETA);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class RelaFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public RelaFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ZoneParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, EssenceZones.RELA);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ClosFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public ClosFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ZoneParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, EssenceZones.CLOS);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ViaFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public ViaFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ZoneParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider, EssenceZones.VIA);
        }
    }

    public static void spawnParticles(ClientWorld world, float points, EssenceZones zoneType, BlockPos startPos, BlockPos endPos, Random random) {
        float k = points / 128;

        if (random.nextDouble() > k * 1/300) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.squaredDistanceTo(startPos.getX(), startPos.getY(), startPos.getZ()) > MathHelper.square(PARTICLE_RADIUS)) return;

        int count = MathHelper.ceil(5 * k);

        MovingParticle.spawnParticles(world, zoneType.getParticleType(), count, 0.5,
                startPos.getX()+0.5, startPos.getY()+0.5, startPos.getZ()+0.5,
                endPos.getX()+0.5, endPos.getY()+0.5, endPos.getZ()+0.5, random);
    }

    public static void spawnParticles(ClientWorld world, float points, EssenceZones zoneType, BlockPos pos, Random random) {
        spawnParticles(world, points, zoneType, pos, new BlockPos(0, -5000, 0), random);
    }

    public static void spawnParticles(ClientWorld world, float points, EssenceZones zoneType, BlockPos pos) {
        spawnParticles(world, points, zoneType, pos, new BlockPos(0, -5000, 0), Random.create());
    }
}
