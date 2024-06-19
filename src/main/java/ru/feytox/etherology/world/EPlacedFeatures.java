package ru.feytox.etherology.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Arrays;

import static ru.feytox.etherology.world.EConfiguredFeatures.PEACH_TREE;

public class EPlacedFeatures {

    public static final RegistryKey<PlacedFeature> PEACH_PLACED_KEY = registerKey("peach_tree_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var lookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, PEACH_PLACED_KEY, lookup.getOrThrow(PEACH_TREE), PlacedFeatures.wouldSurvive(DecoBlocks.PEACH_SAPLING));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new EIdentifier(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        context.register(key, new PlacedFeature(configuration, Arrays.stream(modifiers).toList()));
    }
}
