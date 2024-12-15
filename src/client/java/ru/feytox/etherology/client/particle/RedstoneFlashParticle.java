package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.client.particle.utility.FeyParticle;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;

public class RedstoneFlashParticle extends FeyParticle<SimpleParticleEffect> {

    public RedstoneFlashParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        scale = 0.15f;
        maxAge = 12;

        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
    }
}
