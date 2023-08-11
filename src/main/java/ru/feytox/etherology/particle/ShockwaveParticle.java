package ru.feytox.etherology.particle;

import lombok.val;
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
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
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

        Random random = world.getRandom();
        val effect = ElectricityParticleEffect.of(random, ElectricitySubtype.PEAL);
        effect.spawnParticles(world, random.nextBetween(4, 7), 1, 0, 1, shockPos.add(0, 0.2, 0));
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
