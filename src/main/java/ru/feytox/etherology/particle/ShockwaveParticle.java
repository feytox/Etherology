package ru.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.particle.utility.HorizontalParticle;

public class ShockwaveParticle extends HorizontalParticle {
    private final SpriteProvider spriteProvider;

    public ShockwaveParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        maxAge = 7;
        scale(10);

        this.spriteProvider = spriteProvider;
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
        setSpriteForAge(spriteProvider);
    }

    public static void spawnParticle(ClientWorld world, AbstractClientPlayerEntity player) {
        if (!player.isOnGround()) return;
        float yawAngle = -player.getYaw() * 0.017453292F;
        Vec3d attackVec = new Vec3d(MathHelper.sin(yawAngle), 0, MathHelper.cos(yawAngle));
        Vec3d shockPos = player.getPos().add(attackVec.multiply(1.5)).add(0, 0.025, 0);
        world.addParticle(Etherology.SHOCKWAVE, shockPos.x, shockPos.y, shockPos.z, 0, 0, 0);
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

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ShockwaveParticle(world, x, y, z, spriteProvider);
        }
    }
}
