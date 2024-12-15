package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.client.particle.utility.FeyParticle;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;

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
