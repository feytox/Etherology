package ru.feytox.etherology.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.world.features.zones.EssenceZoneFeatureConfig;

import static ru.feytox.etherology.world.features.zones.EssenceZoneFeature.ESSENCE_ZONE_FEATURE;

public class EConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?,?>> KETA_ZONE_KEY = registerKey("keta_zone");
    public static final RegistryKey<ConfiguredFeature<?,?>> RELA_ZONE_KEY = registerKey("rela_zone");
    public static final RegistryKey<ConfiguredFeature<?,?>> CLOS_ZONE_KEY = registerKey("clos_zone");
    public static final RegistryKey<ConfiguredFeature<?,?>> VIA_ZONE_KEY = registerKey("via_zone");


    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        var placedFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        register(context, KETA_ZONE_KEY, ESSENCE_ZONE_FEATURE, new EssenceZoneFeatureConfig(0));
        register(context, RELA_ZONE_KEY, ESSENCE_ZONE_FEATURE, new EssenceZoneFeatureConfig(1));
        register(context, CLOS_ZONE_KEY, ESSENCE_ZONE_FEATURE, new EssenceZoneFeatureConfig(2));
        register(context, VIA_ZONE_KEY, ESSENCE_ZONE_FEATURE, new EssenceZoneFeatureConfig(3));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new EIdentifier(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
