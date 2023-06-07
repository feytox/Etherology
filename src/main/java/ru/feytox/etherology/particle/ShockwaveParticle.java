package ru.feytox.etherology.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.particle.utility.HorizontalParticle;
import ru.feytox.etherology.util.feyapi.ShockwaveUtil;

public class ShockwaveParticle extends HorizontalParticle {
    private final SpriteProvider spriteProvider;

    public ShockwaveParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        maxAge = 7;
        scale(15);

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

    public static void spawnShockParticles(ClientWorld world, AbstractClientPlayerEntity player) {
        if (!player.isOnGround()) return;
        Vec3d shockPos = ShockwaveUtil.getShockPos(player.getYaw(), player.getPos());
        world.addParticle(Etherology.SHOCKWAVE, shockPos.x, shockPos.y, shockPos.z, 0, 0, 0);

        int pealLevel = EnchantmentHelper.getEquipmentLevel(PealEnchantment.INSTANCE.get(), player);
        if (pealLevel == 0) return;

        for (int i = 0; i < world.random.nextBetween(4, 7); i++) {
            DefaultParticleType electricityType = ElectricityParticle.getParticleType(world.random);
            double ex = shockPos.x + world.random.nextDouble();
            double ez = shockPos.z + world.random.nextDouble();
            world.addParticle(electricityType, ex, shockPos.y + 0.2, ez, 0.5, 0, 10);
        }
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
