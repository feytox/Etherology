package ru.feytox.etherology.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;

import static ru.feytox.etherology.world.EConfiguredFeatures.PEACH_TREE;

public class EPlacedFeatures {
    public static final RegistryKey<PlacedFeature> PEACH_PLACED_KEY = registerKey("peach_tree_placed");


    public static void bootstrap(Registerable<PlacedFeature> context) {
        var lookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, PEACH_PLACED_KEY, lookup.getOrThrow(PEACH_TREE), PlacedFeatures.wouldSurvive(DecoBlocks.PEACH_SAPLING), BiomePlacementModifier.of());
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new EIdentifier(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
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
