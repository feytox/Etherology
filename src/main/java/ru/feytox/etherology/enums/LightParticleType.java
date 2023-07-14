package ru.feytox.etherology.enums;

import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.info.*;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.particle.utility.ParticleInfoProvider;

public enum LightParticleType implements ParticleInfoProvider<LightParticle, LightParticleEffect> {
    SIMPLE(SimpleLightInfo::new),
    SPARK(SparkLightInfo::new),
    VITAL(VitalLightInfo::new),
    PUSHING(PushingLightInfo::new),
    ATTRACT(AttractLightInfo::new),
    BREWING(BrewingLightInfo::new);

    @Nullable
    private final ParticleInfo.Factory<LightParticle, LightParticleEffect> infoFactory;

    LightParticleType(ParticleInfo.@Nullable Factory<LightParticle, LightParticleEffect> infoFactory) {
        this.infoFactory = infoFactory;
    }

    @Override
    @Nullable
    public ParticleInfo.Factory<LightParticle, LightParticleEffect> getFactory() {
        return infoFactory;
    }
}
