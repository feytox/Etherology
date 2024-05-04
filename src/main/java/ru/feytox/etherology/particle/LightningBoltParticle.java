package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.effects.ScalableParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;

public class LightningBoltParticle extends MovingParticle<ScalableParticleEffect> {

    public LightningBoltParticle(ClientWorld clientWorld, double x, double y, double z, ScalableParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);


        scale(6.5f * parameters.getScale());
        maxAge = 12;

        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
    }
}
