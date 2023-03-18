package ru.feytox.etherology.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import ru.feytox.etherology.world.EPlacedFeatures;

public class EssenceZonesGeneration {
    public static void generateZones() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_OCEAN)
                        .or(BiomeSelectors.tag(BiomeTags.IS_RIVER))
                        .or(context -> context.getBiome().getTemperature() <= 0.0f)
                        .or(BiomeSelectors.includeByKey(BiomeKeys.END_MIDLANDS, BiomeKeys.END_HIGHLANDS)),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EPlacedFeatures.KETA_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld()
                        .or(BiomeSelectors.foundInTheNether()),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EPlacedFeatures.RELA_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN)
                        .or(BiomeSelectors.includeByKey(BiomeKeys.END_MIDLANDS, BiomeKeys.END_HIGHLANDS)),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EPlacedFeatures.CLOS_PLACED_KEY
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld()
                        .or(BiomeSelectors.foundInTheNether()),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EPlacedFeatures.VIA_PLACED_KEY
        );
    }
}
