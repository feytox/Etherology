package ru.feytox.etherology.particle.utility;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;

public abstract class FeyParticle<T extends FeyParticleEffect<T>> extends SpriteBillboardParticle {
    protected final T parameters;
    protected final SpriteProvider spriteProvider;

    public FeyParticle(ClientWorld clientWorld, double x, double y, double z, T parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, 0, 0, 0);
        this.parameters = parameters;
        this.spriteProvider = spriteProvider;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }
}
