package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.function.Consumer;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;

public class RecipeGeneration extends FabricRecipeProvider {
    public RecipeGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // attrahite
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ATTRAHITE_NUGGET, 9).input(ATTRAHITE_INGOT).criterion("has_attrahite_ingot", conditionsFromItem(ATTRAHITE_INGOT)).offerTo(exporter, new EIdentifier("attrahite_nugget_from_ingot"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ATTRAHITE_INGOT).pattern("AAA").pattern("AAA").pattern("AAA").input('A', ATTRAHITE_NUGGET).criterion("has_attrahite_nugget", conditionsFromItem(ATTRAHITE_NUGGET)).offerTo(exporter, new EIdentifier("attrahite_ingot_from_nugget"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ATTRAHITE_INGOT, 9).input(ATTRAHITE_BLOCK).criterion("has_attrahite_block", conditionsFromItem(ATTRAHITE_BLOCK.asItem())).offerTo(exporter, new EIdentifier("attrahite_ingot_from_block"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ATTRAHITE_BLOCK.asItem()).pattern("AAA").pattern("AAA").pattern("AAA").input('A', ATTRAHITE_INGOT).criterion("has_attrahite_ingot", conditionsFromItem(ATTRAHITE_INGOT)).offerTo(exporter);
        // ethril
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ETHRIL_NUGGET, 9).input(ETHRIL_INGOT).criterion("has_ethril_ingot", conditionsFromItem(ETHRIL_INGOT)).offerTo(exporter, new EIdentifier("ethril_nugget_from_ingot"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ETHRIL_INGOT).pattern("AAA").pattern("AAA").pattern("AAA").input('A', ETHRIL_NUGGET).criterion("has_ethril_nugget", conditionsFromItem(ETHRIL_NUGGET)).offerTo(exporter, new EIdentifier("ethril_ingot_from_nugget"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ETHRIL_INGOT, 9).input(ETHRIL_BLOCK).criterion("has_ethril_block", conditionsFromItem(ETHRIL_BLOCK.asItem())).offerTo(exporter, new EIdentifier("ethril_ingot_from_block"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ETHRIL_BLOCK.asItem()).pattern("AAA").pattern("AAA").pattern("AAA").input('A', ETHRIL_INGOT).criterion("has_ethril_ingot", conditionsFromItem(ETHRIL_INGOT)).offerTo(exporter);
        // telder steel
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, TELDER_STEEL_NUGGET, 9).input(TELDER_STEEL_INGOT).criterion("has_telder_steel_ingot", conditionsFromItem(TELDER_STEEL_INGOT)).offerTo(exporter, new EIdentifier("telder_steel_nugget_from_ingot"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TELDER_STEEL_INGOT).pattern("AAA").pattern("AAA").pattern("AAA").input('A', TELDER_STEEL_NUGGET).criterion("has_telder_steel_nugget", conditionsFromItem(TELDER_STEEL_NUGGET)).offerTo(exporter, new EIdentifier("telder_steel_ingot_from_nugget"));
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, TELDER_STEEL_INGOT, 9).input(TELDER_STEEL_BLOCK).criterion("has_telder_steel_block", conditionsFromItem(TELDER_STEEL_BLOCK.asItem())).offerTo(exporter, new EIdentifier("telder_steel_ingot_from_block"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TELDER_STEEL_BLOCK.asItem()).pattern("AAA").pattern("AAA").pattern("AAA").input('A', TELDER_STEEL_INGOT).criterion("has_telder_steel_ingot", conditionsFromItem(TELDER_STEEL_INGOT)).offerTo(exporter);
    }
}
