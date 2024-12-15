package ru.feytox.etherology.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.*;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.TriConsumer;
import ru.feytox.etherology.data.EItemTags;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeBuilder;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeBuilder;
import ru.feytox.etherology.recipes.jewelry.LensRecipeBuilder;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipeBuilder;
import ru.feytox.etherology.recipes.matrix.MatrixRecipeBuilder;
import ru.feytox.etherology.recipes.staff.StaffCarpetCuttingRecipe;
import ru.feytox.etherology.recipes.staff.StaffCarpetingRecipe;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.block.Blocks.*;
import static net.minecraft.recipe.book.RecipeCategory.*;
import static ru.feytox.etherology.magic.aspects.Aspect.*;
import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;
import static ru.feytox.etherology.registry.item.ArmorItems.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.EItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class RecipeGeneration extends FabricRecipeProvider {

    public RecipeGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // azel
        offerMaterialBlock(exporter, AZEL_INGOT, AZEL_BLOCK);
        offerMaterialNugget(exporter, AZEL_NUGGET, AZEL_INGOT);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(ATTRAHITE), MISC, ATTRAHITE_BRICK, 0.1F, 200).criterion(has(ATTRAHITE), from(ATTRAHITE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(BUILDING_BLOCKS, ATTRAHITE_BRICKS).input('#', ATTRAHITE_BRICK).pattern("##").pattern("##").criterion(has(ATTRAHITE), from(ATTRAHITE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, RAW_AZEL).input('#', ENRICHED_ATTRAHITE).input('C', CALCITE).pattern("#C").pattern("C#").criterion(has(ATTRAHITE), from(ATTRAHITE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(RAW_AZEL), MISC, AZEL_INGOT, 0.3F, 200).criterion(has(ATTRAHITE), from(ATTRAHITE)).offerTo(exporter);
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(RAW_AZEL), MISC, AZEL_INGOT, 0.3F, 100).criterion(has(ATTRAHITE), from(ATTRAHITE)).offerTo(exporter, getBlastingItemPath(AZEL_INGOT));

        // ethril
        offerMaterialBlock(exporter, ETHRIL_INGOT, ETHRIL_BLOCK);
        offerMaterialNugget(exporter, ETHRIL_NUGGET, ETHRIL_INGOT);

        // ebony
        offerMaterialBlock(exporter, EBONY_INGOT, EBONY_BLOCK);
        offerMaterialNugget(exporter, EBONY_NUGGET, EBONY_INGOT);

        // forest lantern
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(FOREST_LANTERN), FOOD, FOREST_LANTERN_CRUMB, 0.35f, 200).criterion(has(FOREST_LANTERN), from(FOREST_LANTERN)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmoking(Ingredient.ofItems(FOREST_LANTERN), FOOD, FOREST_LANTERN_CRUMB, 0.35f, 100).criterion(has(FOREST_LANTERN), from(FOREST_LANTERN)).offerTo(exporter, getFromPath(FOREST_LANTERN_CRUMB, "smoking"));
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(FOREST_LANTERN), FOOD, FOREST_LANTERN_CRUMB, 0.35f, 600).criterion(has(FOREST_LANTERN), from(FOREST_LANTERN)).offerTo(exporter, getFromPath(FOREST_LANTERN_CRUMB, "campfire"));

        // special recipes
        ComplexRecipeJsonBuilder.create(StaffCarpetingRecipe::new).offerTo(exporter, "staff_carpeting");
        ComplexRecipeJsonBuilder.create(StaffCarpetCuttingRecipe::new).offerTo(exporter, "staff_carpet_cutting");

        // block families recipes
        registerFamilies(EBlockFamilies.FAMILIES, exporter, FeatureSet.of(FeatureFlags.VANILLA));

        // peach
        offerPlanksRecipe(exporter, ExtraBlocksRegistry.PEACH_PLANKS, EItemTags.PEACH_LOGS, 4);
        offerBarkBlockRecipe(exporter, PEACH_WOOD, PEACH_LOG);
        offerBarkBlockRecipe(exporter, STRIPPED_PEACH_WOOD, STRIPPED_PEACH_LOG);
        offerBoatRecipe(exporter, PEACH_BOAT, ExtraBlocksRegistry.PEACH_PLANKS);
        offerChestBoatRecipe(exporter, PEACH_CHEST_BOAT, PEACH_BOAT);
        offerHangingSignRecipe(exporter, DecoBlockItems.PEACH_HANGING_SIGN, STRIPPED_PEACH_LOG);

        // stone families recipes
        offerStonecuttingRecipe(exporter, EBlockFamilies.STONE_FAMILIES);
        offerStonecuttingRecipe(exporter, BUILDING_BLOCKS, POLISHED_SLITHERITE_BRICKS, POLISHED_SLITHERITE);
        offerPolishedStoneRecipe(exporter, BUILDING_BLOCKS, POLISHED_SLITHERITE_BRICKS, POLISHED_SLITHERITE);

        // TODO: 18.08.2024 find a better solution
        // stone -> slitherite
        ShapedRecipeJsonBuilder.create(REDSTONE, COMPARATOR).input('#', REDSTONE_TORCH).input('X', Items.QUARTZ).input('I', SLITHERITE).pattern(" # ").pattern("#X#").pattern("III").criterion(has(Items.QUARTZ), from(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(REDSTONE, REPEATER).input('#', REDSTONE_TORCH).input('X', Items.REDSTONE).input('I', SLITHERITE).pattern("#X#").pattern("III").criterion(has(REDSTONE_TORCH), from(REDSTONE_TORCH)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(DECORATIONS, STONECUTTER).input('I', Items.IRON_INGOT).input('#', SLITHERITE).pattern(" I ").pattern("###").criterion(has(SLITHERITE), from(SLITHERITE)).offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(MISC, THUJA_OIL, 2).input(THUJA_SEEDS).criterion(has(THUJA_SEEDS), from(THUJA_SEEDS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(MISC, BEAMER_SEEDS, 3).input(BEAM_FRUIT).criterion(has(BEAM_FRUIT), from(BEAM_FRUIT)).offerTo(exporter);

        // NOTE: complicated crafts = more than 1 line for recipe

        // tools
        ShapedRecipeJsonBuilder.create(TOOLS, ToolItems.IRON_SHIELD).input('#', Items.IRON_INGOT).input('P', ItemTags.PLANKS).pattern("###").pattern(" P ").criterion(has(Items.IRON_INGOT), from(Items.IRON_INGOT)).offerTo(exporter);
        // TODO: 28.02.2024 try replace to c:stick etc
        // TODO: 28.02.2024 criterion
        registerPicks(exporter);
        ShapedRecipeJsonBuilder.create(TOOLS, STREAM_KEY).input('N', Items.IRON_NUGGET).input('T', EBONY_INGOT).input('I', Items.STICK)
                .pattern("N")
                .pattern("T")
                .pattern("I").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(TOOLS, TUNING_MACE).input('W', RESONATING_WAND).input('I', Items.STICK).input('S', Items.IRON_INGOT)
                .pattern("W W")
                .pattern("WSW")
                .pattern(" I ").criterion(has(RESONATING_WAND), from(RESONATING_WAND)).offerTo(exporter);

        // ebony vanilla items
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, EBONY_HELMET).input('X', EBONY_INGOT).pattern("XXX").pattern("X X").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, EBONY_CHESTPLATE).input('X', EBONY_INGOT).pattern("X X").pattern("XXX").pattern("XXX").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, EBONY_LEGGINGS).input('X', EBONY_INGOT).pattern("XXX").pattern("X X").pattern("X X").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, EBONY_BOOTS).input('X', EBONY_INGOT).pattern("X X").pattern("X X").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, EBONY_AXE).input('#', Items.STICK).input('X', EBONY_INGOT).pattern("XX").pattern("X#").pattern(" #").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, EBONY_PICKAXE).input('#', Items.STICK).input('X', EBONY_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, EBONY_HOE).input('#', Items.STICK).input('X', EBONY_INGOT).pattern("XX").pattern(" #").pattern(" #").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, EBONY_SHOVEL).input('#', Items.STICK).input('X', EBONY_INGOT).pattern("X").pattern("#").pattern("#").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, EBONY_SWORD).input('#', Items.STICK).input('X', EBONY_INGOT).pattern("X").pattern("X").pattern("#").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(EBONY_HELMET, EBONY_CHESTPLATE, EBONY_LEGGINGS, EBONY_BOOTS, EBONY_AXE, EBONY_PICKAXE, EBONY_HOE, EBONY_SHOVEL, EBONY_SWORD), MISC, EBONY_NUGGET, 0.1f, 200).criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter, getSmeltingItemPath(EBONY_NUGGET));
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(EBONY_HELMET, EBONY_CHESTPLATE, EBONY_LEGGINGS, EBONY_BOOTS, EBONY_AXE, EBONY_PICKAXE, EBONY_HOE, EBONY_SHOVEL, EBONY_SWORD), MISC, EBONY_NUGGET, 0.1f, 100).criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter, getBlastingItemPath(EBONY_NUGGET));

        // ethril vanilla items
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ETHRIL_HELMET).input('X', ETHRIL_INGOT).pattern("XXX").pattern("X X").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ETHRIL_CHESTPLATE).input('X', ETHRIL_INGOT).pattern("X X").pattern("XXX").pattern("XXX").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ETHRIL_LEGGINGS).input('X', ETHRIL_INGOT).pattern("XXX").pattern("X X").pattern("X X").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ETHRIL_BOOTS).input('X', ETHRIL_INGOT).pattern("X X").pattern("X X").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ETHRIL_AXE).input('#', Items.STICK).input('X', ETHRIL_INGOT).pattern("XX").pattern("X#").pattern(" #").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ETHRIL_PICKAXE).input('#', Items.STICK).input('X', ETHRIL_INGOT).pattern("XXX").pattern(" # ").pattern(" # ").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ETHRIL_HOE).input('#', Items.STICK).input('X', ETHRIL_INGOT).pattern("XX").pattern(" #").pattern(" #").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ETHRIL_SHOVEL).input('#', Items.STICK).input('X', ETHRIL_INGOT).pattern("X").pattern("#").pattern("#").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ETHRIL_SWORD).input('#', Items.STICK).input('X', ETHRIL_INGOT).pattern("X").pattern("X").pattern("#").criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(ETHRIL_HELMET, ETHRIL_CHESTPLATE, ETHRIL_LEGGINGS, ETHRIL_BOOTS, ETHRIL_AXE, ETHRIL_PICKAXE, ETHRIL_HOE, ETHRIL_SHOVEL, ETHRIL_SWORD), MISC, ETHRIL_NUGGET, 0.1f, 200).criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter, getSmeltingItemPath(ETHRIL_NUGGET));
        CookingRecipeJsonBuilder.createBlasting(Ingredient.ofItems(ETHRIL_HELMET, ETHRIL_CHESTPLATE, ETHRIL_LEGGINGS, ETHRIL_BOOTS, ETHRIL_AXE, ETHRIL_PICKAXE, ETHRIL_HOE, ETHRIL_SHOVEL, ETHRIL_SWORD), MISC, ETHRIL_NUGGET, 0.1f, 100).criterion(has(ETHRIL_INGOT), from(ETHRIL_INGOT)).offerTo(exporter, getBlastingItemPath(ETHRIL_NUGGET));

        // jug
        ShapedRecipeJsonBuilder.create(MISC, CLAY_JUG).input('#', CLAY)
                .pattern("# #")
                .pattern("# #")
                .pattern("###").criterion(has(CLAY), from(CLAY)).offerTo(exporter);
        CookingRecipeJsonBuilder.createSmelting(Ingredient.ofItems(CLAY_JUG), MISC, JUG, 0.35F, 200).criterion(has(CLAY_JUG), from(CLAY_JUG)).offerTo(exporter);

        // "simple" recipe
        ShapedRecipeJsonBuilder.create(MISC, EBlocks.SPILL_BARREL).input('S', ItemTags.WOODEN_SLABS).input('#', ItemTags.PLANKS)
                .pattern("S#S")
                .pattern("# #")
                .pattern("S#S").criterion("has_planks", conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(REDSTONE, ARCANELIGHT_DETECTOR_BLOCK).input('G', GLASS).input('A', Items.AMETHYST_SHARD).input('P', ItemTags.WOODEN_SLABS)
                .pattern("GGG")
                .pattern("AAA")
                .pattern("PPP").criterion(has(Items.AMETHYST_SHARD), from(Items.AMETHYST_SHARD)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, BREWING_CAULDRON).input('#', Items.IRON_INGOT).input('I', Items.STICK).input('C', AZEL_NUGGET)
                .pattern("#I#")
                .pattern("#C#")
                .pattern(" # ").criterion(has(AZEL_INGOT), from(AZEL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, CRATE).input('C', Items.IRON_NUGGET).input('S', ItemTags.WOODEN_SLABS).input('#', ItemTags.PLANKS)
                .pattern("CSC")
                .pattern("#S#").criterion("has_planks", conditionsFromTag(ItemTags.PLANKS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, INVENTOR_TABLE).input('#', Items.IRON_INGOT).input('S', ItemTags.WOODEN_SLABS)
                .pattern("##")
                .pattern("SS")
                .pattern("SS").criterion("has_wooden_slab", conditionsFromTag(ItemTags.WOODEN_SLABS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(DECORATIONS, PEDESTAL_BLOCK, 2).input('S', POLISHED_SLITHERITE_SLAB).input('#', POLISHED_SLITHERITE)
                .pattern("S")
                .pattern("#")
                .pattern("S").criterion(has(SLITHERITE), from(SLITHERITE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(TOOLS, WARP_COUNTER).input('R', Items.REDSTONE).input('#', EBONY_INGOT)
                .pattern(" # ")
                .pattern("#R#")
                .pattern(" # ").criterion(has(EBONY_INGOT), from(EBONY_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, CHANNEL_CASE, 4).input('I', Items.IRON_INGOT).input('W', ItemTags.PLANKS).input('T', THUJA_OIL)
                .pattern("IWI")
                .pattern("WTW")
                .pattern("IWI").criterion(has(THUJA_OIL), from(THUJA_OIL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, Items.LEATHER).input('S', Items.STRING).input('F', FOREST_LANTERN)
                .pattern("SFS")
                .pattern(" F ")
                .pattern("SFS").criterion(has(FOREST_LANTERN), from(FOREST_LANTERN)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, EMPOWERMENT_TABLE).input('A', Items.AMETHYST_SHARD).input('#', ItemTags.PLANKS)
                .pattern("AA")
                .pattern("##")
                .pattern("##").criterion(has(Items.AMETHYST_SHARD), from(Items.AMETHYST_SHARD)).offerTo(exporter);

        // "hard" recipes
        ShapedRecipeJsonBuilder.create(MISC, SPINNER).input('C', Items.IRON_NUGGET).input('I', Items.IRON_INGOT).input('S', EItemTags.SEDIMENTARY_STONES).input('#', SMOOTH_STONE).input('E', ETHEROSCOPE)
                .pattern("CIC")
                .pattern("ISI")
                .pattern("#E#").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_CHANNEL, 2).input('O', THUJA_OIL).input('T', EBONY_INGOT).input('E', ETHEROSCOPE).input('C', Items.IRON_NUGGET)
                .pattern(" O ")
                .pattern("TET")
                .pattern(" C ").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_FORK).input('C', ETHEREAL_CHANNEL).input('I', Items.IRON_INGOT).input('E', ETHEROSCOPE)
                .pattern(" C ")
                .pattern("IEI")
                .pattern(" C ").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_FURNACE).input('I', Items.IRON_INGOT).input('A', AZEL_INGOT).input('B', Items.BLAZE_POWDER).input('C', AZEL_NUGGET).input('E', ETHEROSCOPE)
                .pattern("IAI")
                .pattern("IBI")
                .pattern("CEC").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_SOCKET).input('#', IRON_BARS).input('S', STONE).input('E', ETHEROSCOPE)
                .pattern(" # ")
                .pattern("SES").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, SEDIMENTARY_STONE).input('S', STONE).input('A', AZEL_INGOT).input('R', Items.REDSTONE)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SRS").criterion(has(AZEL_INGOT), from(AZEL_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, ETHEREAL_STORAGE).input('T', EBONY_INGOT).input('N', Items.IRON_NUGGET).input('G', GLINT).input('C', ItemTags.STONE_CRAFTING_MATERIALS).input('E', ETHEROSCOPE)
                .pattern("TTT")
                .pattern("NGN")
                .pattern("CEC").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, JEWELRY_TABLE).input('C', ItemTags.STONE_CRAFTING_MATERIALS).input('I', Items.IRON_INGOT).input('D', DROPPER).input('E', ETHEREAL_CHANNEL)
                .pattern("CIC")
                .pattern("CDC")
                .pattern("CEC").criterion(has(ETHEREAL_CHANNEL), from(ETHEREAL_CHANNEL)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(MISC, LEVITATOR).input('#', ItemTags.LOGS).input('N', Items.IRON_NUGGET).input('F', Items.RABBIT_HIDE).input('L', REDSTONE_LENS).input('E', ETHEROSCOPE)
                .pattern("#N#")
                .pattern("FLF")
                .pattern("#E#").criterion(has(ETHEROSCOPE), from(ETHEROSCOPE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(REDSTONE, TUNING_FORK, 2).input('#', ItemTags.PLANKS).input('R', Items.REDSTONE).input('I', RESONATING_WAND)
                .pattern("IRI")
                .pattern(" # ").criterion(has(RESONATING_WAND), from(RESONATING_WAND)).offerTo(exporter);

        // Etherology recipe types

        // TODO: 23.07.2024 remove
        // test recipes
        MatrixRecipeBuilder.create(Items.IRON_INGOT, ExtraBlocksRegistry.PEACH_PLANKS, 3.0f, PLANTA, ALCHEMA, STRALFA).offerTo(exporter, "test_armillary");

        // brewing
        AlchemyRecipeBuilder.create(RAW_AZEL, BINDER).add(MEMO, 6).add(SOCE, 4).add(FELKA, 4).offerTo(exporter);
        AlchemyRecipeBuilder.create(EBONY, EBONY_INGOT).add(TALO, 3).add(FLIMA, 2).offerTo(exporter);
        AlchemyRecipeBuilder.create(CALCITE, GLINT).add(ETHA, 3).add(MORA, 5).add(AREA, 4).offerTo(exporter);
        AlchemyRecipeBuilder.create(SLITHERITE, UNADJUSTED_LENS).add(FRADO, 5).add(VIBRA, 3).add(HENDALL, 5).offerTo(exporter);

        // empowerment
        EmpowerRecipeBuilder.create(ETHEROSCOPE).via(1).keta(2).input('A', AZEL_INGOT).input('Q', Items.QUARTZ).input('R', Items.REDSTONE)
                .pattern("   ")
                .pattern("AQA")
                .pattern(" R ").offerTo(exporter);
        EmpowerRecipeBuilder.create(OCULUS).empty().input('G', Items.GOLD_INGOT).input('A', Items.AMETHYST_SHARD)
                .pattern(" G ")
                .pattern("GAG")
                .pattern(" G ").offerTo(exporter);
        EmpowerRecipeBuilder.create(REVELATION_VIEW).rella(4).input('G', Items.GOLD_INGOT).input('O', OCULUS).input('L', Items.LEATHER)
                .pattern(" L ")
                .pattern("OGO")
                .pattern("   ").offerTo(exporter);
        EmpowerRecipeBuilder.create(RESONATING_WAND, 2).rella(2).keta(1).input('I', Items.IRON_INGOT).input('S', Items.ECHO_SHARD)
                .pattern(" I ")
                .pattern(" S ")
                .pattern(" I ").offerTo(exporter);
        EmpowerRecipeBuilder.create(SAMOVAR_BLOCK).rella(1).keta(2).input('#', EBONY_INGOT).input('H', Items.HEART_OF_THE_SEA).input('B', Items.BLAZE_POWDER)
                .pattern(" # ")
                .pattern("#H#")
                .pattern(" B ").offerTo(exporter);
        registerStaffs(exporter, STRIPPED_ACACIA_LOG, STRIPPED_BIRCH_LOG, STRIPPED_CRIMSON_STEM, STRIPPED_DARK_OAK_LOG,
                STRIPPED_JUNGLE_LOG, STRIPPED_MANGROVE_LOG, STRIPPED_OAK_LOG, STRIPPED_PEACH_LOG, STRIPPED_SPRUCE_LOG,
                STRIPPED_WARPED_STEM);

        // lens modifiers
        ModifierRecipeBuilder.create(LensModifier.AREA, 12)
                .pattern("00####00")
                .pattern("0######0")
                .pattern("###XX###")
                .pattern("##XX#X##")
                .pattern("##X#XX##")
                .pattern("###XX###")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
        ModifierRecipeBuilder.create(LensModifier.CHARGE, 12)
                .pattern("00####00")
                .pattern("0######0")
                .pattern("##XX####")
                .pattern("##X#XX##")
                .pattern("##XX#X##")
                .pattern("####XX##")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
        ModifierRecipeBuilder.create(LensModifier.CONCENTRATION, 12)
                .pattern("00####00")
                .pattern("0######0")
                .pattern("########")
                .pattern("#XX##XX#")
                .pattern("##X##X##")
                .pattern("###XX###")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
        ModifierRecipeBuilder.create(LensModifier.FILTERING, 12)
                .pattern("00####00")
                .pattern("0######0")
                .pattern("###XXX##")
                .pattern("########")
                .pattern("########")
                .pattern("##XXX###")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
        ModifierRecipeBuilder.create(LensModifier.SAVING, 12)
                .pattern("00####00")
                .pattern("0######0")
                .pattern("#XX#X###")
                .pattern("#X##X###")
                .pattern("###X##X#")
                .pattern("###X#XX#")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
        ModifierRecipeBuilder.create(LensModifier.STREAM, 12)
                .pattern("00####00")
                .pattern("0###X##0")
                .pattern("####X###")
                .pattern("##X#XXX#")
                .pattern("##X#####")
                .pattern("##XXX###")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
        ModifierRecipeBuilder.create(LensModifier.REINFORCEMENT, 12)
                .pattern("00####00")
                .pattern("0####X#0")
                .pattern("#XXXXX##")
                .pattern("##X##X##")
                .pattern("##X##X##")
                .pattern("##XXXXX#")
                .pattern("0#X####0")
                .pattern("00####00").offerTo(exporter);

        // lenses
        LensRecipeBuilder.create(REDSTONE_LENS, 12)
                .pattern("00####00")
                .pattern("0######0")
                .pattern("##XXXX##")
                .pattern("#X####X#")
                .pattern("#X####X#")
                .pattern("#XX##XX#")
                .pattern("0######0")
                .pattern("00####00").offerTo(exporter);
    }

    private void registerStaffs(RecipeExporter exporter, ItemConvertible... strippedLogs) {
        for (ItemConvertible log : strippedLogs) {
            String strippedWood = getItemPath(log.asItem()).replace("_log", "").replace("_stem", "");
            String woodType = strippedWood.replace("stripped_", "");
            if (woodType.equals(strippedWood)) throw new IllegalArgumentException("Expected stripped log, found: " + log);
            ItemStack staffStack = STAFF.getDefaultStack();
            StaffMaterial core = StaffMaterial.valueOf(woodType.toUpperCase());
            staffStack.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component ->
                    component.setPartInfo(new StaffPartInfo(StaffPart.CORE, core, StaffPattern.EMPTY)));
            EmpowerRecipeBuilder.create(staffStack).clos(4).keta(6).via(6).input('I', Items.IRON_INGOT).input('B', BINDER).input('W', log)
                    .pattern(" I ")
                    .pattern("WBW")
                    .pattern(" B ").offerTo(exporter, getItemPath(STAFF) + "_" + woodType);
        }
    }

    private void registerPicks(RecipeExporter exporter) {
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

    private void registerFamilies(List<BlockFamily> blockFamilies, RecipeExporter exporter, FeatureSet enabledFeatures) {
        blockFamilies.stream()
                .filter(BlockFamily::shouldGenerateRecipes)
                .forEach(family -> RecipeProvider.generateFamily(exporter, family, enabledFeatures));
    }

    private void offerStonecuttingRecipe(RecipeExporter exporter, BlockFamily... blockFamilies) {
        Arrays.stream(blockFamilies).forEach(family -> family.getVariants().forEach((variant, block) -> {
            int count = 1;
            RecipeCategory category = BUILDING_BLOCKS;
            boolean exclude = false;

            switch (variant) {
                case SLAB -> count = 2;
                case WALL -> category = DECORATIONS;
                case BUTTON, PRESSURE_PLATE, CRACKED -> exclude = true;
            }

            if (exclude) return;
            offerStonecuttingRecipe(exporter, category, block, family.getBaseBlock(), count);
        }));
    }

    private String has(ItemConvertible itemConvertible) {
        return hasItem(itemConvertible);
    }

    private AdvancementCriterion<InventoryChangedCriterion.Conditions> from(ItemConvertible itemConvertible) {
        return conditionsFromItem(itemConvertible);
    }

    private void offerMaterialBlock(RecipeExporter exporter, ItemConvertible ingot, ItemConvertible block) {
        offerReversibleCompactingRecipesWithReverseRecipeGroup(exporter, MISC, ingot, BUILDING_BLOCKS, block, from(ingot, block), group(ingot));
    }

    private void offerMaterialNugget(RecipeExporter exporter, ItemConvertible nugget, ItemConvertible ingot) {
        offerReversibleCompactingRecipesWithCompactingRecipeGroup(exporter, MISC, nugget, MISC, ingot, from(ingot, nugget), group(ingot));
    }

    private String from(ItemConvertible from, ItemConvertible to) {
        return getItemPath(from) + "_from_" + getItemPath(to);
    }

    private String group(ItemConvertible item) {
        return getItemPath(item);
    }
    
    private Identifier getFromPath(ItemConvertible itemConvertible, String suffix) {
        return EIdentifier.of(getItemPath(itemConvertible) + "_from_" + suffix);
    }
}
