package ru.feytox.etherology.particle.subtypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.SparkParticle;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.info.SparkJewelryInfo;
import ru.feytox.etherology.particle.info.SparkMatrixInfo;
import ru.feytox.etherology.particle.info.SparkRisingInfo;
import ru.feytox.etherology.particle.info.SparkSedimentaryInfo;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.particle.utility.ParticleInfoProvider;

@Getter
@RequiredArgsConstructor
public enum SparkSubtype implements ParticleInfoProvider<SparkParticle, SparkParticleEffect> {
    SIMPLE(null),
    KETA(SparkSedimentaryInfo.of(EssenceZoneType.KETA)),
    RELLA(SparkSedimentaryInfo.of(EssenceZoneType.RELLA)),
    VIA(SparkSedimentaryInfo.of(EssenceZoneType.VIA)),
    CLOS(SparkSedimentaryInfo.of(EssenceZoneType.CLOS)),
    RISING(SparkRisingInfo::new),
    JEWELRY(SparkJewelryInfo::new),
    MATRIX(SparkMatrixInfo::new);

    private final ParticleInfo.Factory<SparkParticle, SparkParticleEffect> factory;

    @Nullable
    public static SparkSubtype of(EssenceZoneType zoneType) {
        return EnumUtils.getEnum(SparkSubtype.class, zoneType.name(), null);
    }
}
