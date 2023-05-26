package ru.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.particle.utility.HorizontalParticle;

public class ShockwaveParticle extends HorizontalParticle {
    private final SpriteProvider spriteProvider;

    public ShockwaveParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        maxAge = 10;
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

    public static void spawnParticle(ServerWorld world, Entity entity) {
        BlockPos pos = entity.getBlockPos();
        world.spawnParticles(Etherology.SHOCKWAVE, pos.getX(), pos.getY()+0.1, pos.getZ(), 1, 0, 0, 0, 0);
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
