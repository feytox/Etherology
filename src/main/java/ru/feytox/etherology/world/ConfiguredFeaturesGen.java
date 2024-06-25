package ru.feytox.etherology.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import ru.feytox.etherology.mixin.TreeConfiguredFeaturesAccessor;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.world.trees.BirchBranchesDecorator;
import ru.feytox.etherology.world.trees.PeachLanternDecorator;
import ru.feytox.etherology.world.trees.PeachWeepingDecorator;

import java.util.Collections;

import static ru.feytox.etherology.registry.world.TreesRegistry.peach;

public class ConfiguredFeaturesGen {
    public static final RegistryKey<ConfiguredFeature<?,?>> PEACH_TREE = registerKey("peach_tree");
    public static final RegistryKey<ConfiguredFeature<?,?>> PEACH_SAPLING_TREE = registerKey("peach_sapling_tree");
    public static final RegistryKey<ConfiguredFeature<?,?>> BIRCH_BRANCH_TREE = registerKey("birch_branch_tree");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        register(context, PEACH_TREE, Feature.TREE,
                peach().decorators(ImmutableList.of(PeachWeepingDecorator.INSTANCE, PeachLanternDecorator.INSTANCE)).build());
        register(context, PEACH_SAPLING_TREE, Feature.TREE,
                peach().decorators(Collections.singletonList(PeachWeepingDecorator.INSTANCE)).build());
        register(context, BIRCH_BRANCH_TREE, Feature.TREE,
                TreeConfiguredFeaturesAccessor.callSuperBirch().decorators(Collections.singletonList(BirchBranchesDecorator.INSTANCE)).build());
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new EIdentifier(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
