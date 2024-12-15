package ru.feytox.etherology.client.particle.utility;

import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public interface ParticleInfoProvider<S, T extends FeyParticle<M>, M extends FeyParticleEffect<M>> {
    @Nullable
    ParticleInfo.Factory<T, M> getFactory(S subtype);
}
