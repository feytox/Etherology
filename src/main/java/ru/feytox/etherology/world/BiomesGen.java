package ru.feytox.etherology.world;

import lombok.val;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import ru.feytox.etherology.mixin.OverworldBiomeCreatorAccessor;
import ru.feytox.etherology.world.biome.EtherBiomes;

public class BiomesGen {

    public static void registerBiomes(Registerable<Biome> context) {
        val featureLookup = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
        val carverLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER);
        addGoldenForest(context, featureLookup, carverLookup);
    }

    private static void addGoldenForest(Registerable<Biome> context, RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
        val baseSpawnerBuilder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(baseSpawnerBuilder);
        DefaultBiomeFeatures.addFarmAnimals(baseSpawnerBuilder);

        val generationBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        OverworldBiomeCreatorAccessor.callAddBasicFeatures(generationBuilder);
        DefaultBiomeFeatures.addDefaultOres(generationBuilder);
        DefaultBiomeFeatures.addDefaultDisks(generationBuilder);
        DefaultBiomeFeatures.addForestGrass(generationBuilder);
        DefaultBiomeFeatures.addDefaultMushrooms(generationBuilder);
        DefaultBiomeFeatures.addDefaultVegetation(generationBuilder);

        generationBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, PlacedFeaturesGen.DISK_COARSE_DIRT);
        generationBuilder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, PlacedFeaturesGen.ETHER_ROCKS);
        generationBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeaturesGen.PEACH_TREES);
        generationBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeaturesGen.BIRCH_BRANCH_TREES);
        generationBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeaturesGen.GOLDEN_FOREST_FLOWERS);
        generationBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeaturesGen.PATCH_LIGHTELET);
        generationBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, PlacedFeaturesGen.PATCH_BEAMER);

        context.register(EtherBiomes.GOLDEN_FOREST, new Biome.Builder()
                .temperature(0.45f)
                .downfall(0.8f)
                .precipitation(true)
                .effects(new BiomeEffects.Builder()
                        .skyColor(9026303).fogColor(13037311).waterColor(3840969)
                        .waterFogColor(1129027).grassColor(5236584).foliageColor(4122414)
                        .moodSound(BiomeMoodSound.CAVE)
                        .music(MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_FOREST))
                        .build())
                .spawnSettings(baseSpawnerBuilder.build())
                .generationSettings(generationBuilder.build())
                .build()
        );
    }
}
