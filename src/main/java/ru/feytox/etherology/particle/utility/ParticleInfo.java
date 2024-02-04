package ru.feytox.etherology.particle.utility;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.util.feyapi.RGBColor;

public abstract class ParticleInfo<T extends FeyParticle<M>, M extends FeyParticleEffect<M>> {

    public ParticleInfo(ClientWorld clientWorld, double x, double y, double z, M parameters, SpriteProvider spriteProvider) {}

    public float getScale(Random random) {
        return 1.0f;
    }

    @Nullable
    abstract public RGBColor getStartColor(Random random);

    public void extraInit(T particle) {
        particle.setSpriteForAge();
    }

    abstract public void tick(T particle);

    abstract public int getMaxAge(Random random);

    @FunctionalInterface
    public interface Factory<T extends FeyParticle<M>, M extends FeyParticleEffect<M>> {

        ParticleInfo<T, M> createInfo(ClientWorld clientWorld, double x, double y, double z, M parameters, SpriteProvider spriteProvider);
    }
}
