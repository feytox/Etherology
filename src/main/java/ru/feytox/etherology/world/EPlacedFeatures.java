package ru.feytox.etherology.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;

public class EPlacedFeatures {
    public static final RegistryKey<PlacedFeature> KETA_PLACED_KEY = registerKey("keta_zone_placed");
    public static final RegistryKey<PlacedFeature> RELA_PLACED_KEY = registerKey("rela_zone_placed");
    public static final RegistryKey<PlacedFeature> CLOS_PLACED_KEY = registerKey("clos_zone_placed");
    public static final RegistryKey<PlacedFeature> VIA_PLACED_KEY = registerKey("via_zone_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, KETA_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(EConfiguredFeatures.KETA_ZONE_KEY),
                RarityFilterPlacementModifier.of(5), CountPlacementModifier.of(1), BiomePlacementModifier.of());
        register(context, RELA_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(EConfiguredFeatures.RELA_ZONE_KEY),
                RarityFilterPlacementModifier.of(5), CountPlacementModifier.of(1), BiomePlacementModifier.of());
        register(context, CLOS_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(EConfiguredFeatures.CLOS_ZONE_KEY),
                RarityFilterPlacementModifier.of(5), CountPlacementModifier.of(1), BiomePlacementModifier.of());
        register(context, VIA_PLACED_KEY, configuredFeatureRegistryEntryLookup.getOrThrow(EConfiguredFeatures.VIA_ZONE_KEY),
                RarityFilterPlacementModifier.of(5), CountPlacementModifier.of(1), BiomePlacementModifier.of());
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new EIdentifier(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }
    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return modifiers(CountPlacementModifier.of(count), heightModifier);
    }
    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }
}
