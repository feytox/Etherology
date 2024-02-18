package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.util.RecipesRegistry;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Arrays;
import java.util.List;
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

        // special recipes
        ComplexRecipeJsonBuilder.create(RecipesRegistry.STAFF_CARPET).offerTo(exporter, "staff_carpeting");
        ComplexRecipeJsonBuilder.create(RecipesRegistry.STAFF_CARPET_CUT).offerTo(exporter, "staff_carpet_cutting");

        // block families recipes
        registerFamilies(EBlockFamilies.FAMILIES, exporter, FeatureSet.of(FeatureFlags.VANILLA));

        // peach
        offerPlanksRecipe(exporter, PEACH_PLANKS, ItemTagGeneration.PEACH_LOGS, 4);
        offerBarkBlockRecipe(exporter, PEACH_WOOD, PEACH_LOG);
        offerBarkBlockRecipe(exporter, STRIPPED_PEACH_WOOD, STRIPPED_PEACH_LOG);

        // ethereal stones
        offerStonecuttingRecipe(exporter, EBlockFamilies.ETHEREAL_STONE, EBlockFamilies.COBBLED_ETHEREAL_STONE, EBlockFamilies.CRACKED_ETHEREAL_STONE_BRICKS, EBlockFamilies.CHISELED_ETHEREAL_STONE_BRICKS, EBlockFamilies.ETHEREAL_STONE_BRICKS, EBlockFamilies.MOSSY_COBBLED_ETHEREAL_STONE, EBlockFamilies.MOSSY_ETHEREAL_STONE_BRICKS, EBlockFamilies.POLISHED_ETHEREAL_STONE);

        // stone -> ethereal stone
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.COMPARATOR).input('#', Blocks.REDSTONE_TORCH).input('X', Items.QUARTZ).input('I', ETHEREAL_STONE).pattern(" # ").pattern("#X#").pattern("III").criterion("has_quartz", conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.REPEATER).input('#', Blocks.REDSTONE_TORCH).input('X', Items.REDSTONE).input('I', ETHEREAL_STONE).pattern("#X#").pattern("III").criterion("has_redstone_torch", conditionsFromItem(Blocks.REDSTONE_TORCH)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ETHEREAL_STONE_BRICKS, 4).input('#', ETHEREAL_STONE).pattern("##").pattern("##").criterion("has_ethereal_stone", conditionsFromItem(ETHEREAL_STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, Blocks.STONECUTTER).input('I', Items.IRON_INGOT).input('#', ETHEREAL_STONE).pattern(" I ").pattern("###").criterion("has_ethereal_stone", conditionsFromItem(ETHEREAL_STONE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(COBBLED_ETHEREAL_STONE), RecipeCategory.BUILDING_BLOCKS, ETHEREAL_STONE, 0.1F, 200).criterion("has_cobbled_ethereal_stone", conditionsFromItem(COBBLED_ETHEREAL_STONE)).offerTo(exporter);
        
        // cobblestone -> cobbled ethereal stone
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, Blocks.DISPENSER).input('R', Items.REDSTONE).input('#', COBBLED_ETHEREAL_STONE).input('X', Items.BOW).pattern("###").pattern("#X#").pattern("#R#").criterion("has_bow", conditionsFromItem(Items.BOW)).offerTo(exporter);

        // clay tile
        offerStonecuttingRecipe(exporter, EBlockFamilies.CLAY_TILE, EBlockFamilies.BLUE_CLAY_TILE, EBlockFamilies.GREEN_CLAY_TILE, EBlockFamilies.RED_CLAY_TILE, EBlockFamilies.YELLOW_CLAY_TILE);
        offer2x2Recipe(exporter, RecipeCategory.BUILDING_BLOCKS, CLAY_TILES, Blocks.TERRACOTTA, 4);
        offer2x2Recipe(exporter, RecipeCategory.BUILDING_BLOCKS, BLUE_CLAY_TILES, Blocks.BLUE_TERRACOTTA, 4);
        offer2x2Recipe(exporter, RecipeCategory.BUILDING_BLOCKS, GREEN_CLAY_TILES, Blocks.GREEN_TERRACOTTA, 4);
        offer2x2Recipe(exporter, RecipeCategory.BUILDING_BLOCKS, RED_CLAY_TILES, Blocks.RED_TERRACOTTA, 4);
        offer2x2Recipe(exporter, RecipeCategory.BUILDING_BLOCKS, YELLOW_CLAY_TILES, Blocks.YELLOW_TERRACOTTA, 4);

    }

    private void registerFamilies(List<BlockFamily> blockFamilies, Consumer<RecipeJsonProvider> exporter, FeatureSet enabledFeatures) {
        blockFamilies.stream()
                .filter(family -> family.shouldGenerateRecipes(enabledFeatures))
                .forEach(family -> RecipeProvider.generateFamily(exporter, family));
    }

    private void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, BlockFamily... blockFamilies) {
        Arrays.stream(blockFamilies).forEach(family -> family.getVariants().forEach((variant, block) -> {
            int count = 1;
            RecipeCategory category = RecipeCategory.BUILDING_BLOCKS;
            boolean exclude = false;

            switch (variant) {
                case SLAB -> count = 2;
                case WALL -> category = RecipeCategory.DECORATIONS;
                case BUTTON, PRESSURE_PLATE -> exclude = true;
            }

            if (exclude) return;
            offerStonecuttingRecipe(exporter, category, block, family.getBaseBlock(), count);
        }));
    }

    private void offer2x2Recipe(Consumer<RecipeJsonProvider> exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
        ShapedRecipeJsonBuilder.create(category, output, count).input('#', input).pattern("##").pattern("##").criterion(hasItem(input), conditionsFromItem(input)).offerTo(exporter);
    }
}
