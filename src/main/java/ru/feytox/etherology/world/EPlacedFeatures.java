package ru.feytox.etherology.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;
import ru.feytox.etherology.DecoBlocks;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;

import static ru.feytox.etherology.world.EConfiguredFeatures.*;

public class EPlacedFeatures {
    public static final RegistryKey<PlacedFeature> KETA_PLACED_KEY = registerKey("keta_zone_placed");
    public static final RegistryKey<PlacedFeature> RELA_PLACED_KEY = registerKey("rela_zone_placed");
    public static final RegistryKey<PlacedFeature> CLOS_PLACED_KEY = registerKey("clos_zone_placed");
    public static final RegistryKey<PlacedFeature> VIA_PLACED_KEY = registerKey("via_zone_placed");
    public static final RegistryKey<PlacedFeature> PEACH_PLACED_KEY = registerKey("peach_tree_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var lookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        // zones
        registerZone(context, lookup, KETA_PLACED_KEY, KETA_ZONE_KEY, 5, 1);
        registerZone(context, lookup, RELA_PLACED_KEY, RELA_ZONE_KEY, 5, 1);
        registerZone(context, lookup, CLOS_PLACED_KEY, CLOS_ZONE_KEY, 5, 1);
        registerZone(context, lookup, VIA_PLACED_KEY, VIA_ZONE_KEY, 5, 1);

        // trees
        register(context, PEACH_PLACED_KEY, lookup.getOrThrow(PEACH_TREE), PlacedFeatures.wouldSurvive(DecoBlocks.PEACH_SAPLING), BiomePlacementModifier.of());
    }

    private static void registerZone(Registerable<PlacedFeature> context, RegistryEntryLookup<ConfiguredFeature<?, ?>> lookup, RegistryKey<PlacedFeature> key, RegistryKey<ConfiguredFeature<?, ?>> configuredKey, int rarity, int count) {
        register(context, key, lookup.getOrThrow(configuredKey), RarityFilterPlacementModifier.of(rarity), CountPlacementModifier.of(count), BiomePlacementModifier.of());
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
