package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.world.BiomesGen;
import ru.feytox.etherology.world.ConfiguredFeaturesGen;
import ru.feytox.etherology.world.PlacedFeaturesGen;
import ru.feytox.etherology.world.StructuresGen;

public class DataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(WorldGeneration::new);
        pack.addProvider(ModelGeneration::new);
        pack.addProvider(RuLangGeneration::new);
        pack.addProvider(RecipeGeneration::new);
        pack.addProvider(BlockLootTableGeneration::new);
        BlockTagGeneration blockTagGeneration = pack.addProvider(BlockTagGeneration::new);
        pack.addProvider(((output, registries) -> new ItemTagGeneration(output, registries, blockTagGeneration)));
        pack.addProvider(GameEventTagGeneration::new);
    }

    /**
     * @see WorldGeneration#configure(RegistryWrapper.WrapperLookup, FabricDynamicRegistryProvider.Entries) 
     */
    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeaturesGen::registerFeatures);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PlacedFeaturesGen::registerFeatures);
        registryBuilder.addRegistry(RegistryKeys.STRUCTURE_SET, StructuresGen::registerStructureSets);
        registryBuilder.addRegistry(RegistryKeys.STRUCTURE, StructuresGen::registerStructures);
        registryBuilder.addRegistry(RegistryKeys.TEMPLATE_POOL, StructuresGen::registerTemplates);
        registryBuilder.addRegistry(RegistryKeys.BIOME, BiomesGen::registerBiomes);
    }
}
