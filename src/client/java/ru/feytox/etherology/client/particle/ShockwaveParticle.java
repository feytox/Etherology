package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.client.particle.utility.HorizontalParticle;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;

public class ShockwaveParticle extends HorizontalParticle<SimpleParticleEffect> {
    public ShockwaveParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        maxAge = 7;
        scale(15.0f);
        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
    }
}
