package ru.feytox.etherology.registry.world;

import com.terraformersmc.biolith.api.biome.BiomePlacement;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
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

import java.util.Optional;

@UtilityClass
public class WorldGenRegistry {

    public static final Feature<DefaultFeatureConfig> ETHER_ROCK = registerFeature("ether_rock", new EtherRockFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> THUJA = registerFeature("thuja", new ThujaFeature(DefaultFeatureConfig.CODEC));
    public static final RegistryKey<Biome> GOLDEN_FOREST = of("golden_forest");

    public static final PlacementModifierType<StructurePlacementModifier> STRUCTURE_PLACEMENT_MODIFIER = PlacementModifierTypeAccessor.callRegister(EIdentifier.strId("structure_placement_modifier"), StructurePlacementModifier.CODEC);
    public static final StructurePoolElementType<RotatedPoolElement> ROTATED_POOL_ELEMENT = StructurePoolElementTypeAccessor.callRegister(EIdentifier.strId("rotated_pool_element"), RotatedPoolElement.CODEC);

    public static void registerWorldGen() {
        TreesRegistry.registerTrees();
        registerModifications();
        registerBiomes();
    }

    public static Optional<RegistryKey<PlacedFeature>> getGoldenForestBonemeal(ServerWorld world, BlockPos pos) {
        return world.getBiome(pos).matchesKey(GOLDEN_FOREST) ? Optional.of(PlacedFeaturesGen.GOLDEN_FOREST_BONEMEAL) : Optional.empty();
    }

    // TODO: 19.07.2024 remove
    // /execute positioned ~2000 ~ ~2000 run locate biome etherology:golden_forest
    private static void registerBiomes() {
        BiomePlacement.replaceOverworld(BiomeKeys.BIRCH_FOREST, GOLDEN_FOREST, 0.3f);
        BiomePlacement.replaceOverworld(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, GOLDEN_FOREST, 0.3f);
    }

    private static void registerModifications() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                PlacedFeaturesGen.ATTRAHITE);
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA),
                GenerationStep.Feature.VEGETAL_DECORATION,
                PlacedFeaturesGen.PATCH_THUJA);
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature) {
        return Registry.register(Registries.FEATURE, name, feature);
    }

    private static RegistryKey<Biome> of(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, EIdentifier.of(name));
    }
}
