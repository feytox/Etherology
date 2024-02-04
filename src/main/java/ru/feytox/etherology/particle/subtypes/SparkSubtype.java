package ru.feytox.etherology.particle.subtypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.SparkParticle;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.info.SedimentarySparkInfo;
import ru.feytox.etherology.particle.info.SparkRisingInfo;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.particle.utility.ParticleInfoProvider;

@Getter
@RequiredArgsConstructor
public enum SparkSubtype implements ParticleInfoProvider<SparkParticle, SparkParticleEffect> {
    SIMPLE(null),
    KETA(SedimentarySparkInfo.of(EssenceZoneType.KETA)),
    RELLA(SedimentarySparkInfo.of(EssenceZoneType.RELLA)),
    VIA(SedimentarySparkInfo.of(EssenceZoneType.VIA)),
    CLOS(SedimentarySparkInfo.of(EssenceZoneType.CLOS)),
    RISING(SparkRisingInfo::new);

    private final ParticleInfo.Factory<SparkParticle, SparkParticleEffect> factory;

    @Nullable
    public static SparkSubtype of(EssenceZoneType zoneType) {
        return EnumUtils.getEnum(SparkSubtype.class, zoneType.name(), null);
    }
}
