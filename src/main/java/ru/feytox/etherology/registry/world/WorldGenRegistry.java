package ru.feytox.etherology.registry.world;

import com.terraformersmc.biolith.api.biome.BiomePlacement;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
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
import java.util.function.Predicate;

import static net.fabricmc.fabric.api.biome.v1.BiomeSelectors.*;
import static ru.feytox.etherology.world.PlacedFeaturesGen.*;

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

    private static void registerBiomes() {
        BiomePlacement.replaceOverworld(BiomeKeys.BIRCH_FOREST, GOLDEN_FOREST, 0.3f);
        BiomePlacement.replaceOverworld(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, GOLDEN_FOREST, 0.3f);
    }

    private static void registerModifications() {
        BiomeModifications.addFeature(
                foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                PlacedFeaturesGen.ATTRAHITE);
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(BiomeKeys.TAIGA, BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA),
                GenerationStep.Feature.VEGETAL_DECORATION,
                PlacedFeaturesGen.PATCH_THUJA);

        addSeal(foundInOverworld(), RELLA_SEAL);
        addSeal(foundInTheNether(), RELLA_SEAL_VERY_RARE);
        addSeal(tag(BiomeTags.IS_MOUNTAIN), CLOS_SEAL);
        addSeal(foundInTheEnd(), CLOS_SEAL_RARE);
        addSeal(foundInOverworld(), VIA_SEAL);
        addSeal(foundInOverworld().and(ctx -> ctx.getBiome().getTemperature() >= 1.0f), VIA_SEAL_RARE);
        addSeal(foundInTheNether(), VIA_SEAL_RARE2);
        addSeal(foundInOverworld().and(ctx -> ctx.getBiome().getTemperature() <= 0.05f), KETA_SEAL);
        addSeal(tag(BiomeTags.WATER_ON_MAP_OUTLINES), KETA_SEAL_RARE);
        addSeal(foundInTheEnd(), KETA_SEAL_VERY_RARE);
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String name, F feature) {
        return Registry.register(Registries.FEATURE, name, feature);
    }

    private static RegistryKey<Biome> of(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, EIdentifier.of(name));
    }

    private static void addSeal(Predicate<BiomeSelectionContext> selector, SealKey sealKey) {
        BiomeModifications.addFeature(selector, GenerationStep.Feature.VEGETAL_DECORATION, sealKey.placedFeatureKey());
    }
}
