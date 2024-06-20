package ru.feytox.etherology.world;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import ru.feytox.etherology.registry.world.TreesRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;

public class EConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?,?>> PEACH_TREE = registerKey("peach_tree");
    public static final RegistryKey<ConfiguredFeature<?,?>> PEACH_SAPLING_TREE = registerKey("peach_sapling_tree");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, PEACH_TREE, Feature.TREE, TreesRegistry.PEACH_CONFIG);
        register(context, PEACH_SAPLING_TREE, Feature.TREE, TreesRegistry.PEACH_SAPLING_CONFIG);
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new EIdentifier(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
