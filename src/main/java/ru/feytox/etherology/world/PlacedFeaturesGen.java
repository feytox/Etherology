package ru.feytox.etherology.world;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placementmodifier.*;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.world.feature.StructurePlacementModifier;

import java.util.Arrays;
import java.util.List;

import static ru.feytox.etherology.world.ConfiguredFeaturesGen.*;

@UtilityClass
public class PlacedFeaturesGen {

    public static final RegistryKey<PlacedFeature> PEACH_TREES = of("peach_trees");
    public static final RegistryKey<PlacedFeature> BIRCH_BRANCH_TREES = of("birch_branch_trees");
    public static final RegistryKey<PlacedFeature> GOLDEN_FOREST_FLOWERS = of("golden_forest_flowers");
    public static final RegistryKey<PlacedFeature> PATCH_LIGHTELET = of("patch_lightelet");
    public static final RegistryKey<PlacedFeature> DISK_COARSE_DIRT = of("disk_coarse_dirt");
    public static final RegistryKey<PlacedFeature> ETHER_ROCKS = of("ether_rocks");
    public static final RegistryKey<PlacedFeature> PATCH_BEAMER = of("patch_beamer");
    public static final RegistryKey<PlacedFeature> PATCH_THUJA = of("patch_thuja");
    public static final RegistryKey<PlacedFeature> ATTRAHITE = of("attrahite");
    public static final RegistryKey<PlacedFeature> GOLDEN_FOREST_BONEMEAL = of("golden_forest_bonemeal");
    public static final RegistryKey<PlacedFeature> GOLDEN_FOREST_RED_MUSHROOM = of("golden_forest_red_mushroom");
    public static final RegistryKey<PlacedFeature> GOLDEN_FOREST_BROWN_MUSHROOM = of("golden_forest_brown_mushroom");

    public static void registerFeatures(Registerable<PlacedFeature> context) {
        var lookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, PEACH_TREES, lookup.getOrThrow(PEACH_TREE),
                CountPlacementModifier.of(new WeightedListIntProvider(DataPool.<IntProvider>builder()
                        .add(ConstantIntProvider.create(1), 9)
                        .add(ConstantIntProvider.create(2), 1)
                        .build())),
                SquarePlacementModifier.of(),
                SurfaceWaterDepthFilterPlacementModifier.of(0),
                HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR),
                BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(DecoBlocks.PEACH_SAPLING.getDefaultState(), BlockPos.ORIGIN)),
                StructurePlacementModifier.of(StructuresGen.ETHER_MONOLITH, 8, true),
                BiomePlacementModifier.of()
        );
        register(context, BIRCH_BRANCH_TREES, lookup.getOrThrow(BIRCH_BRANCH_TREE),
                CountPlacementModifier.of(new WeightedListIntProvider(DataPool.<IntProvider>builder()
                        .add(ConstantIntProvider.create(1), 9)
                        .add(ConstantIntProvider.create(2), 1)
                        .build())),
                SquarePlacementModifier.of(),
                SurfaceWaterDepthFilterPlacementModifier.of(0),
                HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR),
                BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(Blocks.BIRCH_SAPLING.getDefaultState(), BlockPos.ORIGIN)),
                StructurePlacementModifier.of(StructuresGen.ETHER_MONOLITH, 8, true),
                BiomePlacementModifier.of()
        );
        register(context, GOLDEN_FOREST_FLOWERS, lookup.getOrThrow(ConfiguredFeaturesGen.GOLDEN_FOREST_FLOWERS),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING),
                CountPlacementModifier.of(UniformIntProvider.create(0, 1)),
                BiomePlacementModifier.of()
        );
        register(context, PATCH_LIGHTELET, lookup.getOrThrow(ConfiguredFeaturesGen.PATCH_LIGHTELET),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                CountPlacementModifier.of(2),
                BiomePlacementModifier.of()
        );
        register(context, DISK_COARSE_DIRT, lookup.getOrThrow(ConfiguredFeaturesGen.DISK_COARSE_DIRT),
                RarityFilterPlacementModifier.of(1),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                CountPlacementModifier.of(UniformIntProvider.create(0, 1)),
                BiomePlacementModifier.of()
        );
        register(context, ETHER_ROCKS, lookup.getOrThrow(ETHER_ROCK),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING),
                CountPlacementModifier.of(UniformIntProvider.create(1, 2)),
                BiomePlacementModifier.of()
        );
        register(context, PATCH_BEAMER, lookup.getOrThrow(ConfiguredFeaturesGen.PATCH_BEAMER),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                CountPlacementModifier.of(UniformIntProvider.create(0, 1)),
                StructurePlacementModifier.of(StructuresGen.ETHER_MONOLITH, 16, false),
                BiomePlacementModifier.of()
        );
        register(context, PATCH_THUJA, lookup.getOrThrow(ConfiguredFeaturesGen.PATCH_THUJA),
                RarityFilterPlacementModifier.of(7),
                SquarePlacementModifier.of(),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(DecoBlocks.THUJA_PLANT.getDefaultState(), BlockPos.ORIGIN)),
                CountPlacementModifier.of(UniformIntProvider.create(0, 1)),
                BiomePlacementModifier.of()
        );
        register(context, ATTRAHITE, lookup.getOrThrow(ConfiguredFeaturesGen.ATTRAHITE),
                RarityFilterPlacementModifier.of(10),
                SquarePlacementModifier.of(),
                HeightRangePlacementModifier.of(UniformHeightProvider.create(YOffset.fixed(-58), YOffset.fixed(-8))),
                BiomePlacementModifier.of()
        );
        register(context, GOLDEN_FOREST_BONEMEAL, lookup.getOrThrow(SINGLE_PIECE_OF_GRASS_LIGHTELET), PlacedFeatures.isAir());
        register(context, GOLDEN_FOREST_RED_MUSHROOM, lookup.getOrThrow(VegetationConfiguredFeatures.PATCH_RED_MUSHROOM), mushroomModifiers());
        register(context, GOLDEN_FOREST_BROWN_MUSHROOM, lookup.getOrThrow(VegetationConfiguredFeatures.PATCH_BROWN_MUSHROOM), mushroomModifiers());
    }

    public static RegistryKey<PlacedFeature> of(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, EIdentifier.of(name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        register(context, key, configuration, Arrays.stream(modifiers).toList());
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, modifiers));
    }

    /**
     * @see VegetationPlacedFeatures#mushroomModifiers(int, PlacementModifier)
     */
    private static List<PlacementModifier> mushroomModifiers() {
        return List.of(RarityFilterPlacementModifier.of(5),
                HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                SquarePlacementModifier.of(),
                BiomePlacementModifier.of());
    }
}
