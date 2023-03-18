package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import ru.feytox.etherology.ItemsRegistry;
import ru.feytox.etherology.items.glints.AbstractGlintItem;

public class ModelGeneration extends FabricModelProvider {
    public ModelGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerGlint(ItemsRegistry.GLINT, itemModelGenerator);
    }

    private static void registerGlint(AbstractGlintItem glint, ItemModelGenerator itemModelGenerator) {
        for (int i = 1; i < 17; i++) {
            itemModelGenerator.register(glint, "_"+i, Models.GENERATED);
        }
    }
}
