package name.uwu.feytox.etherology.particle;

import name.uwu.feytox.etherology.util.feyapi.FeyRandom;
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

import static name.uwu.feytox.etherology.Etherology.ZONE_PARTICLE;

public class ZoneParticle extends MovingParticle {
    public static final int PARTICLE_RADIUS = 8;
    private final SpriteProvider spriteProvider;

    protected ZoneParticle(ClientWorld clientWorld, double d, double e, double f, SpriteProvider spriteProvider) {
        super(clientWorld, d, e, f, d, e, f);
        this.endX += FeyRandom.getValueSign(this.random) * this.random.nextDouble() * 3;
        this.endY += FeyRandom.getValueSign(this.random) * this.random.nextDouble() * 2;
        this.endZ += FeyRandom.getValueSign(this.random) * this.random.nextDouble() * 3;

        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.maxAge = 20 * this.random.nextBetween(1, 2);
        scale(0.15f);
        this.setRGB(15, 161, 207);
    }

    @Override
    public void tick() {
        acceleratedMovingTick(0.2f, 0.3f);
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
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ZoneParticle(world, x, y, z, spriteProvider);
        }
    }

    public static void spawnParticles(ClientWorld world, float points, BlockPos pos) {
        float k = points / 128;
        Random random = Random.create();

        if (random.nextDouble() > k * 1/320) return;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > MathHelper.square(PARTICLE_RADIUS)) return;

        int count = MathHelper.ceil(5 * k);

        MovingParticle.spawnParticles(world, ZONE_PARTICLE, count, 0.5,
                pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5,
                pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, random);
    }
}
