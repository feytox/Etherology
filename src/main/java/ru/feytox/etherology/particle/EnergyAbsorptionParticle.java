package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.utility.FeyParticle;

/**
 * @see net.minecraft.client.particle.FireworksSparkParticle.Flash
 */
public class EnergyAbsorptionParticle extends FeyParticle<SimpleParticleEffect> {

    public EnergyAbsorptionParticle(ClientWorld clientWorld, double x, double y, double z, SimpleParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        maxAge = 4;
        setSpriteForAge();
        scale(3.0f);
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
    }
}
