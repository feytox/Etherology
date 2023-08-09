package ru.feytox.etherology.gui.teldecore;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.gui.teldecore.pages.CraftPage;
import ru.feytox.etherology.gui.teldecore.pages.DoublePage;
import ru.feytox.etherology.gui.teldecore.pages.EtherPage;
import ru.feytox.etherology.gui.teldecore.pages.FurnacePage;
import ru.feytox.etherology.recipes.visual.TCraftRecipe;
import ru.feytox.etherology.recipes.visual.TEtherRecipe;
import ru.feytox.etherology.recipes.visual.TFurnaceRecipe;

import java.util.Map;

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

        rootComponent.child(new EmptyBook());
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

    private VerticalFlowLayout getTestPage_furnace() {
        return new DoublePage(
                new FurnacePage(true, new TFurnaceRecipe(
                        Ingredient.fromTag(ItemTags.PLANKS), 100, Items.DIAMOND_ORE.getDefaultStack())),
                new FurnacePage(false, new TFurnaceRecipe(
                        Items.LOOM.getDefaultStack(), 666, Items.END_CRYSTAL.getDefaultStack())));
    }
}
