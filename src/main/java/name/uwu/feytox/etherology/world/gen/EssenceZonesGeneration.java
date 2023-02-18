package name.uwu.feytox.etherology.world.gen;

import name.uwu.feytox.etherology.world.EPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.gen.GenerationStep;

public class EssenceZonesGeneration {
    public static void generateZones() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld()
                        .and(BiomeSelectors.tag(BiomeTags.IS_OCEAN).negate())
                        .and(BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN).negate()),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EPlacedFeatures.KETA_PLACED_KEY
        );
    }
}
