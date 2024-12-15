package ru.feytox.etherology.client.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.client.particle.utility.FeyParticle;
import ru.feytox.etherology.particle.effects.ScalableParticleEffect;

/**
 * @see net.minecraft.client.particle.SweepAttackParticle
 */
public class ScalableSweepParticle extends FeyParticle<ScalableParticleEffect> {

    public ScalableSweepParticle(ClientWorld clientWorld, double x, double y, double z, ScalableParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        maxAge = 4;
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        red = f;
        green = f;
        blue = f;
        scale = parameters.getScale();
        setSpriteForAge();
    }

    public int getBrightness(float tint) {
        return 15728880;
    }

    public void tick() {
        if (tickAge()) return;
        markPrevPos();
        setSpriteForAge();
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }
}
