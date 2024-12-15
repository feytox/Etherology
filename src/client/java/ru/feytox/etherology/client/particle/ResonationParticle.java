package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.client.particle.utility.MovingParticle;
import ru.feytox.etherology.particle.effects.ScalableParticleEffect;

public class ResonationParticle extends MovingParticle<ScalableParticleEffect> {

    public ResonationParticle(ClientWorld clientWorld, double x, double y, double z, ScalableParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        maxAge = 10;
        scale(8.0f * parameters.getScale());
        setSpriteForAge();
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
    }
}
