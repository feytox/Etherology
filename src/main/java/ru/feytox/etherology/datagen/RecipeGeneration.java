package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.*;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.apache.logging.log4j.util.TriConsumer;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.RecipesRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.block.Blocks.*;
import static net.minecraft.recipe.book.RecipeCategory.*;
import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.EItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class RecipeGeneration extends FabricRecipeProvider {
    public RecipeGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // azel
        ShapelessRecipeJsonBuilder.create(MISC, AZEL_NUGGET, 9).input(AZEL_INGOT).criterion("has_azel_ingot", conditionsFromItem(AZEL_INGOT)).offerTo(exporter, new EIdentifier("azel_nugget_from_ingot"));
        ShapedRecipeJsonBuilder.create(MISC, AZEL_INGOT).pattern("AAA").pattern("AAA").pattern("AAA").input('A', AZEL_NUGGET).criterion("has_azel_nugget", conditionsFromItem(AZEL_NUGGET)).offerTo(exporter, new EIdentifier("azel_ingot_from_nugget"));
        ShapelessRecipeJsonBuilder.create(MISC, AZEL_INGOT, 9).input(AZEL_BLOCK).criterion("has_azel_block", conditionsFromItem(AZEL_BLOCK.asItem())).offerTo(exporter, new EIdentifier("azel_ingot_from_block"));
        ShapedRecipeJsonBuilder.create(MISC, AZEL_BLOCK.asItem()).pattern("AAA").pattern("AAA").pattern("AAA").input('A', AZEL_INGOT).criterion("has_azel_ingot", conditionsFromItem(AZEL_INGOT)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(ATTRAHITE), MISC, ATTRAHITE_BRICK, 0.1F, 200).criterion("has_attrahite", conditionsFromItem(ATTRAHITE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(BUILDING_BLOCKS, ATTRAHITE_BRICKS).input('#', ATTRAHITE_BRICK).pattern("##").pattern("##").criterion("has_attrahite", conditionsFromItem(ATTRAHITE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, RAW_AZEL).input('#', ENRICHED_ATTRAHITE).input('C', CALCITE).pattern("#C").pattern("C#").criterion("has_attrahite", conditionsFromItem(ATTRAHITE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(RAW_AZEL), MISC, AZEL_INGOT, 0.3F, 200).criterion("has_attrahite", conditionsFromItem(ATTRAHITE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(RAW_AZEL), MISC, AZEL_INGOT, 0.3F, 100).criterion("has_attrahite", conditionsFromItem(ATTRAHITE)).offerTo(exporter, getBlastingItemPath(AZEL_INGOT));
        offerStonecuttingRecipe(exporter, EBlockFamilies.ATTRAHITE_BRICKS);

        // ethril
        ShapelessRecipeJsonBuilder.create(MISC, ETHRIL_NUGGET, 9).input(ETHRIL_INGOT).criterion("has_ethril_ingot", conditionsFromItem(ETHRIL_INGOT)).offerTo(exporter, new EIdentifier("ethril_nugget_from_ingot"));
        ShapedRecipeJsonBuilder.create(MISC, ETHRIL_INGOT).pattern("AAA").pattern("AAA").pattern("AAA").input('A', ETHRIL_NUGGET).criterion("has_ethril_nugget", conditionsFromItem(ETHRIL_NUGGET)).offerTo(exporter, new EIdentifier("ethril_ingot_from_nugget"));
        ShapelessRecipeJsonBuilder.create(MISC, ETHRIL_INGOT, 9).input(ETHRIL_BLOCK).criterion("has_ethril_block", conditionsFromItem(ETHRIL_BLOCK.asItem())).offerTo(exporter, new EIdentifier("ethril_ingot_from_block"));
        ShapedRecipeJsonBuilder.create(MISC, ETHRIL_BLOCK.asItem()).pattern("AAA").pattern("AAA").pattern("AAA").input('A', ETHRIL_INGOT).criterion("has_ethril_ingot", conditionsFromItem(ETHRIL_INGOT)).offerTo(exporter);

        // ebony
        ShapelessRecipeJsonBuilder.create(MISC, EBONY_NUGGET, 9).input(EBONY_INGOT).criterion("has_ebony_ingot", conditionsFromItem(EBONY_INGOT)).offerTo(exporter, new EIdentifier("ebony_nugget_from_ingot"));
        ShapedRecipeJsonBuilder.create(MISC, EBONY_INGOT).pattern("AAA").pattern("AAA").pattern("AAA").input('A', EBONY_NUGGET).criterion("has_ebony_nugget", conditionsFromItem(EBONY_NUGGET)).offerTo(exporter, new EIdentifier("ebony_ingot_from_nugget"));
        ShapelessRecipeJsonBuilder.create(MISC, EBONY_INGOT, 9).input(EBONY_BLOCK).criterion("has_ebony_block", conditionsFromItem(EBONY_BLOCK.asItem())).offerTo(exporter, new EIdentifier("ebony_ingot_from_block"));
        ShapedRecipeJsonBuilder.create(MISC, EBONY_BLOCK.asItem()).pattern("AAA").pattern("AAA").pattern("AAA").input('A', EBONY_INGOT).criterion("has_ebony_ingot", conditionsFromItem(EBONY_INGOT)).offerTo(exporter);

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
        ShapedRecipeJsonBuilder.create(REDSTONE, COMPARATOR).input('#', REDSTONE_TORCH).input('X', Items.QUARTZ).input('I', ETHEREAL_STONE).pattern(" # ").pattern("#X#").pattern("III").criterion("has_quartz", conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(REDSTONE, REPEATER).input('#', REDSTONE_TORCH).input('X', Items.REDSTONE).input('I', ETHEREAL_STONE).pattern("#X#").pattern("III").criterion("has_redstone_torch", conditionsFromItem(REDSTONE_TORCH)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(BUILDING_BLOCKS, ETHEREAL_STONE_BRICKS, 4).input('#', ETHEREAL_STONE).pattern("##").pattern("##").criterion("has_ethereal_stone", conditionsFromItem(ETHEREAL_STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(DECORATIONS, STONECUTTER).input('I', Items.IRON_INGOT).input('#', ETHEREAL_STONE).pattern(" I ").pattern("###").criterion("has_ethereal_stone", conditionsFromItem(ETHEREAL_STONE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(COBBLED_ETHEREAL_STONE), BUILDING_BLOCKS, ETHEREAL_STONE, 0.1F, 200).criterion("has_cobbled_ethereal_stone", conditionsFromItem(COBBLED_ETHEREAL_STONE)).offerTo(exporter);
        
        // cobblestone -> cobbled ethereal stone
        ShapedRecipeJsonBuilder.create(REDSTONE, DISPENSER).input('R', Items.REDSTONE).input('#', COBBLED_ETHEREAL_STONE).input('X', Items.BOW).pattern("###").pattern("#X#").pattern("#R#").criterion("has_bow", conditionsFromItem(Items.BOW)).offerTo(exporter);

        // clay tile
        offerStonecuttingRecipe(exporter, EBlockFamilies.CLAY_TILE, EBlockFamilies.BLUE_CLAY_TILE, EBlockFamilies.GREEN_CLAY_TILE, EBlockFamilies.RED_CLAY_TILE, EBlockFamilies.YELLOW_CLAY_TILE);
        offer2x2Recipe(exporter, BUILDING_BLOCKS, CLAY_TILES, TERRACOTTA, 4);
        offer2x2Recipe(exporter, BUILDING_BLOCKS, BLUE_CLAY_TILES, BLUE_TERRACOTTA, 4);
        offer2x2Recipe(exporter, BUILDING_BLOCKS, GREEN_CLAY_TILES, GREEN_TERRACOTTA, 4);
        offer2x2Recipe(exporter, BUILDING_BLOCKS, RED_CLAY_TILES, RED_TERRACOTTA, 4);
        offer2x2Recipe(exporter, BUILDING_BLOCKS, YELLOW_CLAY_TILES, YELLOW_TERRACOTTA, 4);

        ShapelessRecipeJsonBuilder.create(MISC, THUJA_OIL, 2).input(THUJA_SEEDS).criterion("has_thuja_seeds", conditionsFromItem(THUJA_SEEDS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(MISC, BEAMER_SEEDS, 3).input(BEAM_FRUIT).criterion("has_beam_fruit", conditionsFromItem(BEAM_FRUIT)).offerTo(exporter);

        // NOTE: complicated crafts = more than 1 line for recipe

        // tools
        ShapedRecipeJsonBuilder.create(TOOLS, ToolItems.IRON_SHIELD).input('#', Items.IRON_INGOT).input('P', ItemTags.PLANKS).pattern("###").pattern(" P ").criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        // TODO: 28.02.2024 try replace to c:stick etc
        // TODO: 28.02.2024 criterion
        registerPicks(exporter);
        ShapedRecipeJsonBuilder.create(TOOLS, STREAM_KEY).input('N', Items.IRON_NUGGET).input('T', EBONY_INGOT).input('I', Items.STICK)
                .pattern("N")
                .pattern("T")
                .pattern("I").criterion("has_teldet_steel_ingot", conditionsFromItem(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(TOOLS, TUNING_MACE).input('W', RESONATING_WAND).input('I', Items.STICK).input('S', Items.IRON_INGOT)
                .pattern("W W")
                .pattern("WSW")
                .pattern(" I ").criterion("has_resonating_wand", conditionsFromItem(RESONATING_WAND)).offerTo(exporter);

        // furniture
        ShapelessRecipeJsonBuilder.create(MISC, SHELF_SLAB).input(Items.ITEM_FRAME).input(FURNITURE_SLAB).criterion("has_furniture_slab", conditionsFromItem(FURNITURE_SLAB)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(MISC, CLOSET_SLAB).input(Items.CHEST).input(FURNITURE_SLAB).criterion("has_furniture_slab", conditionsFromItem(FURNITURE_SLAB)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, FURNITURE_SLAB, 2).input('#', ItemTags.PLANKS).input('I', Items.STICK)
                .pattern("#I#")
                .pattern("#I#").criterion("has_planks", conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);

        // "simple" recipe
        ShapedRecipeJsonBuilder.create(MISC, EBlocks.SPILL_BARREL).input('S', ItemTags.WOODEN_SLABS).input('#', ItemTags.PLANKS)
                .pattern("S#S")
                .pattern("# #")
                .pattern("S#S").criterion("has_planks", conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(REDSTONE, ESSENCE_DETECTOR_BLOCK).input('G', GLASS).input('A', Items.AMETHYST_SHARD).input('P', ItemTags.WOODEN_SLABS)
                .pattern("GGG")
                .pattern("AAA")
                .pattern("PPP").criterion("has_amethyst_shard", conditionsFromItem(Items.AMETHYST_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, BREWING_CAULDRON).input('#', Items.IRON_INGOT).input('I', Items.STICK).input('C', AZEL_NUGGET)
                .pattern("#I#")
                .pattern("#C#")
                .pattern(" # ").criterion("has_azel_ingot", conditionsFromItem(AZEL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, CRATE).input('C', Items.IRON_NUGGET).input('S', ItemTags.WOODEN_SLABS).input('#', ItemTags.PLANKS)
                .pattern("CSC")
                .pattern("#S#").criterion("has_planks", conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, INVENTOR_TABLE).input('#', Items.IRON_INGOT).input('S', ItemTags.WOODEN_SLABS)
                .pattern("##")
                .pattern("SS")
                .pattern("SS").criterion("has_wooden_slab", conditionsFromTag(ItemTags.WOODEN_SLABS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(DECORATIONS, PEDESTAL_BLOCK, 2).input('S', POLISHED_ETHEREAL_STONE_SLAB).input('#', POLISHED_ETHEREAL_STONE)
                .pattern("S")
                .pattern("#")
                .pattern("S").criterion("has_ethereal_stone", conditionsFromItem(ETHEREAL_STONE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(TOOLS, WARP_COUNTER).input('R', Items.REDSTONE).input('#', EBONY_INGOT)
                .pattern(" # ")
                .pattern("#R#")
                .pattern(" # ").criterion("has_ebony_ingot", conditionsFromItem(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_CHANNEL_CASE, 4).input('I', Items.IRON_INGOT).input('W', ItemTags.PLANKS).input('T', THUJA_OIL)
                .pattern("IWI")
                .pattern("WTW")
                .pattern("IWI").criterion("has_thuja_oil", conditionsFromItem(THUJA_OIL)).offerTo(exporter);

        // "hard" recipes
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_SPINNER).input('C', Items.IRON_NUGGET).input('I', Items.IRON_INGOT).input('S', SEDIMENTARY_BLOCK).input('#', SMOOTH_STONE).input('E', ETHEROSCOPE)
                .pattern("CIC")
                .pattern("ISI")
                .pattern("#E#").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_CHANNEL, 2).input('O', THUJA_OIL).input('T', EBONY_INGOT).input('E', ETHEROSCOPE).input('C', Items.IRON_NUGGET)
                .pattern(" O ")
                .pattern("TET")
                .pattern(" C ").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_FORK).input('C', ETHEREAL_CHANNEL).input('I', Items.IRON_INGOT).input('E', ETHEROSCOPE)
                .pattern(" C ")
                .pattern("IEI")
                .pattern(" C ").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_FURNACE).input('I', Items.IRON_INGOT).input('A', AZEL_INGOT).input('B', Items.BLAZE_POWDER).input('C', AZEL_NUGGET).input('E', ETHEROSCOPE)
                .pattern("IAI")
                .pattern("IBI")
                .pattern("CEC").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_SOCKET).input('#', IRON_BARS).input('S', STONE).input('E', ETHEROSCOPE)
                .pattern(" # ")
                .pattern("SES").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, SEDIMENTARY_BLOCK).input('S', STONE).input('A', AZEL_INGOT).input('R', Items.REDSTONE)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SRS").criterion("has_azel_ingot", conditionsFromItem(AZEL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_STORAGE).input('T', EBONY_INGOT).input('N', Items.IRON_NUGGET).input('G', GLINT).input('C', ItemTags.STONE_CRAFTING_MATERIALS).input('E', ETHEROSCOPE)
                .pattern("TTT")
                .pattern("NGN")
                .pattern("CEC").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, JEWELRY_TABLE).input('C', ItemTags.STONE_CRAFTING_MATERIALS).input('I', Items.IRON_INGOT).input('D', DROPPER).input('E', ETHEREAL_CHANNEL)
                .pattern("CIC")
                .pattern("CDC")
                .pattern("CEC").criterion("has_ethereal_channel", conditionsFromItem(ETHEREAL_CHANNEL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, LEVITATOR).input('#', ItemTags.LOGS).input('N', Items.IRON_NUGGET).input('F', Items.RABBIT_HIDE).input('L', REDSTONE_LENS).input('E', ETHEROSCOPE)
                .pattern("#N#")
                .pattern("FLF")
                .pattern("#E#").criterion("has_etheroscope", conditionsFromItem(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(REDSTONE, TUNING_FORK, 2).input('#', ItemTags.PLANKS).input('R', Items.REDSTONE).input('I', RESONATING_WAND)
                .pattern("IRI")
                .pattern(" # ").criterion("has_resonating_wand", conditionsFromItem(RESONATING_WAND)).offerTo(exporter);
    }

    private void registerPicks(Consumer<RecipeJsonProvider> exporter) {
        registerTools(ToolItems.BATTLE_PICKAXES, (tool, material, criterionPredicate) ->
                ShapedRecipeJsonBuilder.create(TOOLS, tool).input('I', Items.STICK).input('M', material)
                .pattern("MM ")
                .pattern(" IM")
                .pattern(" I ")
                .criterion("has_material", conditionsFromItemPredicates(criterionPredicate)).offerTo(exporter));
    }

    private void registerTools(Item[] toolsArr, TriConsumer<ToolItem, Ingredient, ItemPredicate> registrar) {
        Arrays.stream(toolsArr).forEach(item -> {
            ToolItem tool = (ToolItem) item;
            Ingredient material = tool.getMaterial().getRepairIngredient();
            Item[] materialItems = Arrays.stream(material.getMatchingStacks()).map(ItemStack::getItem).toArray(Item[]::new);
            ItemPredicate criterionPredicate = ItemPredicate.Builder.create().items(materialItems).build();
            registrar.accept(tool, material, criterionPredicate);
        });
    }

    private void registerFamilies(List<BlockFamily> blockFamilies, Consumer<RecipeJsonProvider> exporter, FeatureSet enabledFeatures) {
        blockFamilies.stream()
                .filter(family -> family.shouldGenerateRecipes(enabledFeatures))
                .forEach(family -> RecipeProvider.generateFamily(exporter, family));
    }

    private void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, BlockFamily... blockFamilies) {
        Arrays.stream(blockFamilies).forEach(family -> family.getVariants().forEach((variant, block) -> {
            int count = 1;
            RecipeCategory category = BUILDING_BLOCKS;
            boolean exclude = false;

            switch (variant) {
                case SLAB -> count = 2;
                case WALL -> category = DECORATIONS;
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
