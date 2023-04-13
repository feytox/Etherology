package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.family.BlockFamily;
import ru.feytox.etherology.ItemsRegistry;
import ru.feytox.etherology.items.glints.AbstractGlintItem;

import java.util.Arrays;

import static ru.feytox.etherology.EBlockFamilies.*;

public class ModelGeneration extends FabricModelProvider {
    public ModelGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        // ethereal stones
        registerBlockFamilies(generator, ETHEREAL_STONE, COBBLED_ETHEREAL_STONE, CRACKED_ETHEREAL_STONE_BRICKS, ETHEREAL_STONE_BRICKS, MOSSY_COBBLED_ETHEREAL_STONE, POLISHED_ETHEREAL_STONE, CHISELED_ETHEREAL_STONE_BRICKS, MOSSY_ETHEREAL_STONE_BRICKS);
        // clay tiles
        registerBlockFamilies(generator, CLAY_TILE, BLUE_CLAY_TILE, GREEN_CLAY_TILE, RED_CLAY_TILE, YELLOW_CLAY_TILE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerGlint(ItemsRegistry.GLINT, itemModelGenerator);
    }

    private static void registerBlockFamilies(BlockStateModelGenerator generator, BlockFamily... blockFamilies) {
        Arrays.stream(blockFamilies).forEach(family -> generator.registerCubeAllModelTexturePool(family.getBaseBlock()).family(family));
    }

    private static void registerGlint(AbstractGlintItem glint, ItemModelGenerator itemModelGenerator) {
        for (int i = 1; i < 17; i++) {
            itemModelGenerator.register(glint, "_"+i, Models.GENERATED);
        }
    }
}
