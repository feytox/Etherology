package ru.feytox.etherology.registry.world;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.world.feature.EtherRockFeature;

@UtilityClass
public class WorldGenRegistry {

    public static final RegistryKey<PlacedFeature> ATTRAHITE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new EIdentifier("attrahite"));

    public static final Feature<DefaultFeatureConfig> ETHER_ROCK = registerFeature("ether_rock", new EtherRockFeature(DefaultFeatureConfig.CODEC));

    public static void registerWorldGen() {
        TreesRegistry.registerTrees();
        registerOres();
    }

    private static void registerOres() {
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ATTRAHITE_PLACED_KEY);
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature) {
        return Registry.register(Registries.FEATURE, name, feature);
    }
}
