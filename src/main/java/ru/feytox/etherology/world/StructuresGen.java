package ru.feytox.etherology.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.world.biome.EtherBiomes;
import ru.feytox.etherology.world.structure.RotatedPoolElement;

import java.util.Map;
import java.util.Optional;

@UtilityClass
public class StructuresGen {

    // TODO: 30.06.2024 remove
    // /execute positioned ~2500 ~ ~2500 run locate structure etherology:ether_monolith

    private static final RegistryKey<StructureSet> ETHER_MONOLITHS = of("ether_monoliths", RegistryKeys.STRUCTURE_SET);
    public static final RegistryKey<Structure> ETHER_MONOLITH = of("ether_monolith", RegistryKeys.STRUCTURE);
    private static final RegistryKey<StructurePool> ETHER_MONOLITH_START_POOL = of("ether_monolith_start_pool", RegistryKeys.TEMPLATE_POOL);
    private static final RegistryKey<StructurePool> ETHER_MONOLITH_POOL = of("ether_monolith_pool", RegistryKeys.TEMPLATE_POOL);

    public static void registerStructureSets(Registerable<StructureSet> context) {
        val structureLookup = context.getRegistryLookup(RegistryKeys.STRUCTURE);
        context.register(ETHER_MONOLITHS, new StructureSet(
                structureLookup.getOrThrow(ETHER_MONOLITH),
                new RandomSpreadStructurePlacement(16, 8, SpreadType.LINEAR, 69696969)
        ));
    }

    public static void registerStructures(Registerable<Structure> context) {
        val biomeLookup = context.getRegistryLookup(RegistryKeys.BIOME);
        val poolLookup = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        context.register(ETHER_MONOLITH, new JigsawStructure(
                createConfig(RegistryEntryList.of(
                        biomeLookup.getOrThrow(EtherBiomes.GOLDEN_FOREST)),
                        Map.of(), GenerationStep.Feature.LOCAL_MODIFICATIONS, StructureTerrainAdaptation.BEARD_THIN),
                poolLookup.getOrThrow(ETHER_MONOLITH_START_POOL),
                Optional.of(new EIdentifier("start")),
                2,
                ConstantHeightProvider.ZERO,
                false,
                Optional.of(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES),
                80
        ));
    }

    public static void registerTemplates(Registerable<StructurePool> context) {
        val poolLookup = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        context.register(ETHER_MONOLITH_POOL, new StructurePool(
                poolLookup.getOrThrow(StructurePools.EMPTY),
                ImmutableList.of(
                        Pair.of(StructurePoolElement.ofSingle(EIdentifier.strId("ether_monolith/pillar_0")), 1),
                        Pair.of(StructurePoolElement.ofSingle(EIdentifier.strId("ether_monolith/pillar_1")), 1),
                        Pair.of(StructurePoolElement.ofSingle(EIdentifier.strId("ether_monolith/pillar_2")), 1)
                ), StructurePool.Projection.RIGID));
        context.register(ETHER_MONOLITH_START_POOL, new StructurePool(
                poolLookup.getOrThrow(StructurePools.EMPTY),
                ImmutableList.of(
                        Pair.of(RotatedPoolElement.of(new EIdentifier("ether_monolith/start"), BlockRotation.NONE), 1)
                ), StructurePool.Projection.RIGID));
    }

    private static <T> RegistryKey<T> of(String name, RegistryKey<? extends Registry<T>> registry) {
        return RegistryKey.of(registry, new EIdentifier(name));
    }

    private static Structure.Config createConfig(RegistryEntryList<Biome> biomes, Map<SpawnGroup, StructureSpawns> spawns, GenerationStep.Feature featureStep, StructureTerrainAdaptation terrainAdaptation) {
        return new Structure.Config(biomes, spawns, featureStep, terrainAdaptation);
    }
}
