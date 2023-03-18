package ru.feytox.etherology.world.features.zones;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;
import ru.feytox.etherology.magic.zones.EssenceZones;

public record EssenceZoneFeatureConfig(int zoneNum) implements FeatureConfig {
    public EssenceZoneFeatureConfig(int zoneNum) {
        this.zoneNum = zoneNum;
    }

    public static Codec<EssenceZoneFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codecs.NONNEGATIVE_INT.fieldOf("zoneNum").forGetter(EssenceZoneFeatureConfig::zoneNum))
                            .apply(instance, EssenceZoneFeatureConfig::new));

    public EssenceZones zone() {
        return EssenceZones.getByNum(zoneNum);
    }

    public int zoneNum() {
        return zoneNum;
    }
}
