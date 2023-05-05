package ru.feytox.etherology.gui.teldecore;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.enums.InstabTypes;
import ru.feytox.etherology.enums.MixTypes;
import ru.feytox.etherology.gui.teldecore.pages.*;
import ru.feytox.etherology.recipes.visual.*;

import java.util.Map;

import static ru.feytox.etherology.registry.item.EItems.DEEP_SHARD;

public class TestScreen extends BaseOwoScreen<FlowLayout> {

    public TestScreen() {
        super();
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        rootComponent.child(new EmptyBook().child(
                getTestPage_trans()
                        .positioning(Positioning.absolute(12, 7))
        ));
    }

    private VerticalFlowLayout getTestPage_craft() {
        Map<Integer, Ingredient> recipe_1 = Map.of(
                1, Ingredient.fromTag(ItemTags.PLANKS),
                2, Ingredient.fromTag(ItemTags.PLANKS),
                3, Ingredient.fromTag(ItemTags.PLANKS),
                4, Ingredient.fromTag(ItemTags.PLANKS),
                5, Ingredient.fromTag(ItemTags.PLANKS),
                6, Ingredient.fromTag(ItemTags.PLANKS),
                7, Ingredient.fromTag(ItemTags.PLANKS),
                8, Ingredient.fromTag(ItemTags.PLANKS),
                9, Ingredient.fromTag(ItemTags.PLANKS)
        );

        Map<Integer, Ingredient> recipe_2 = Map.of(
                1, Ingredient.ofItems(Items.ACACIA_BOAT),
                3, Ingredient.ofItems(Items.NETHERITE_HOE),
                5, Ingredient.ofItems(Items.ACACIA_BOAT),
                7, Ingredient.ofItems(Items.NETHERITE_HOE),
                9, Ingredient.ofItems(Items.ACACIA_BOAT)
        );

        return new DoublePage(
                new CraftPage(true, new TCraftRecipe(recipe_1, Items.NETHER_STAR.getDefaultStack())),
                new CraftPage(false, new TCraftRecipe(recipe_2, Items.PIG_SPAWN_EGG.getDefaultStack()))
        );
    }

    private VerticalFlowLayout getTestPage_ether() {
        Map<Integer, Ingredient> recipe_1 = Map.of(
                1, Ingredient.fromTag(ItemTags.PLANKS),
                2, Ingredient.fromTag(ItemTags.PLANKS),
                3, Ingredient.fromTag(ItemTags.PLANKS),
                4, Ingredient.fromTag(ItemTags.PLANKS),
                5, Ingredient.fromTag(ItemTags.PLANKS),
                6, Ingredient.fromTag(ItemTags.PLANKS),
                7, Ingredient.fromTag(ItemTags.PLANKS),
                8, Ingredient.fromTag(ItemTags.PLANKS),
                9, Ingredient.fromTag(ItemTags.PLANKS)
        );

        Map<Integer, Ingredient> recipe_2 = Map.of(
                1, Ingredient.ofItems(Items.ACACIA_BOAT),
                3, Ingredient.ofItems(Items.NETHERITE_HOE),
                5, Ingredient.ofItems(Items.ACACIA_BOAT),
                7, Ingredient.ofItems(Items.NETHERITE_HOE),
                9, Ingredient.ofItems(Items.ACACIA_BOAT)
        );

        return new DoublePage(
                new EtherPage(true, new TEtherRecipe(recipe_1, Items.AMETHYST_SHARD.getDefaultStack(),
                        12, 3, 4, 1)),
                new EtherPage(false, new TEtherRecipe(recipe_2, Items.PIG_SPAWN_EGG.getDefaultStack(),
                        1, 0, 3, 5))
        );
    }

    private VerticalFlowLayout getTestPage_alchemy() {
        Map<Integer, Ingredient> recipe_1 = Map.of(
                1, Ingredient.fromTag(ItemTags.PLANKS),
                2, Ingredient.ofItems(Items.ACACIA_BOAT),
                3, Ingredient.ofItems(Items.ACACIA_BOAT),
                5, Ingredient.fromTag(ItemTags.PLANKS),
                6, Ingredient.ofItems(Items.ACACIA_BOAT),
                8, Ingredient.fromTag(ItemTags.PLANKS),
                9, Ingredient.ofItems(DEEP_SHARD),
                10, Ingredient.fromTag(ItemTags.PLANKS),
                11, Ingredient.ofItems(DEEP_SHARD),
                12, Ingredient.ofItems(DEEP_SHARD)
        );
        Map<Integer, MixTypes> recipe_1_2 = Map.of(
                4, MixTypes.GREEN,
                7, MixTypes.BLUE
        );

        Map<Integer, Ingredient> recipe_2 = Map.of(
                1, Ingredient.ofItems(Items.ACACIA_BOAT),
                2, Ingredient.ofItems(Items.NETHERITE_HOE),
                3, Ingredient.ofItems(DEEP_SHARD),
                5, Ingredient.ofItems(Items.ACACIA_BOAT)
        );
        Map<Integer, MixTypes> recipe_2_2 = Map.of(
                4, MixTypes.MAGENTA
        );

        return new DoublePage(
                new AlchemyPage(true, new TAlchemyRecipe(recipe_1, recipe_1_2, Items.GILDED_BLACKSTONE.getDefaultStack())),
                new AlchemyPage(false, new TAlchemyRecipe(recipe_2, recipe_2_2, Items.COMMAND_BLOCK_MINECART.getDefaultStack()))
        );
    }

    private VerticalFlowLayout getTestPage_furnace() {
        return new DoublePage(
                new FurnacePage(true, new TFurnaceRecipe(
                        Ingredient.fromTag(ItemTags.PLANKS), 100, Items.DIAMOND_ORE.getDefaultStack())),
                new FurnacePage(false, new TFurnaceRecipe(
                        Items.LOOM.getDefaultStack(), 666, Items.END_CRYSTAL.getDefaultStack())));
    }

    private VerticalFlowLayout getTestPage_trans() {
        Map<Integer, Ingredient> recipe_1 = Map.of(
                1, Ingredient.fromTag(ItemTags.PLANKS),
                2, Ingredient.fromTag(ItemTags.PLANKS),
                3, Ingredient.fromTag(ItemTags.PLANKS),
                4, Ingredient.fromTag(ItemTags.PLANKS),
                5, Ingredient.fromTag(ItemTags.PLANKS),
                6, Ingredient.fromTag(ItemTags.PLANKS),
                7, Ingredient.fromTag(ItemTags.PLANKS),
                8, Ingredient.fromTag(ItemTags.PLANKS),
                9, Ingredient.fromTag(ItemTags.PLANKS)
        );

        Map<Integer, Ingredient> recipe_2 = Map.of(
                1, Ingredient.ofItems(Items.ACACIA_BOAT),
                3, Ingredient.ofItems(Items.NETHERITE_HOE),
                5, Ingredient.ofItems(Items.ACACIA_BOAT),
                7, Ingredient.ofItems(Items.NETHERITE_HOE),
                9, Ingredient.ofItems(Items.ACACIA_BOAT)
        );

        return new DoublePage(
                new TransPage(true, new TTransRecipe(recipe_1, InstabTypes.LOW, 9,
                        Items.PRISMARINE_BRICKS.getDefaultStack())),
                new TransPage(false, new TTransRecipe(recipe_2, InstabTypes.HIGH, 25,
                        Items.DIAMOND_BLOCK.getDefaultStack()))
        );
    }
}
