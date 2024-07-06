package ru.feytox.etherology.registry.world;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import ru.feytox.etherology.mixin.PlacementModifierTypeAccessor;
import ru.feytox.etherology.mixin.StructurePoolElementTypeAccessor;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.world.PlacedFeaturesGen;
import ru.feytox.etherology.world.feature.EtherRockFeature;
import ru.feytox.etherology.world.feature.StructurePlacementModifier;
import ru.feytox.etherology.world.feature.ThujaFeature;
import ru.feytox.etherology.world.structure.RotatedPoolElement;

@UtilityClass
public class WorldGenRegistry {

    public static final RegistryKey<PlacedFeature> ATTRAHITE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, EIdentifier.of("attrahite"));

    public static final Feature<DefaultFeatureConfig> ETHER_ROCK = registerFeature("ether_rock", new EtherRockFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> THUJA = registerFeature("thuja", new ThujaFeature(DefaultFeatureConfig.CODEC));

    public static final PlacementModifierType<StructurePlacementModifier> STRUCTURE_PLACEMENT_MODIFIER = PlacementModifierTypeAccessor.callRegister(EIdentifier.strId("structure_placement_modifier"), StructurePlacementModifier.CODEC);
    public static final StructurePoolElementType<RotatedPoolElement> ROTATED_POOL_ELEMENT = StructurePoolElementTypeAccessor.callRegister(EIdentifier.strId("rotated_pool_element"), RotatedPoolElement.CODEC);

    public static void registerWorldGen() {
        TreesRegistry.registerTrees();
        registerModifications();
    }

    private static void registerModifications() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ATTRAHITE_PLACED_KEY);
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA),
                GenerationStep.Feature.VEGETAL_DECORATION,
                PlacedFeaturesGen.PATCH_THUJA);
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature) {
        return Registry.register(Registries.FEATURE, name, feature);
    }
}
