package ru.feytox.etherology.particle.utility;

import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;

public interface ParticleInfoProvider<T extends FeyParticle<M>, M extends FeyParticleEffect<M>> {
    @Nullable
    ParticleInfo.Factory<T, M> getFactory();
}
