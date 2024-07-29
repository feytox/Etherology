package ru.feytox.etherology.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.component.ComponentChanges;
import net.minecraft.recipe.RecipeEntry;
import ru.feytox.etherology.block.jewelryTable.JewelryTableScreen;
import ru.feytox.etherology.compat.emi.EtherEMIPlugin;
import ru.feytox.etherology.compat.emi.misc.FeyEmiRecipe;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.jewelry.AbstractJewelryRecipe;
import ru.feytox.etherology.recipes.jewelry.LensRecipe;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipe;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class JewelryERecipe extends FeyEmiRecipe {

    private final LensPattern pattern;

    protected JewelryERecipe(List<EmiIngredient> inputs, List<EmiStack> outputs, RecipeEntry<? extends AbstractJewelryRecipe> recipeEntry) {
        super(inputs, outputs, recipeEntry.id());
        pattern = recipeEntry.value().getPattern().pattern();
    }

    @Override
    public int getDisplayWidth() {
        return 180;
    }

    @Override
    public int getDisplayHeight() {
        return 100;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addDrawable(25, 2, 96, 96, (draw, mouseX, mouseY, delta) ->
                JewelryTableScreen.renderGrid(draw, pattern, 0, 0));

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 125, 41);
        widgets.addSlot(EmiIngredient.of(inputs), 1, 42);
        widgets.addSlot(EmiIngredient.of(outputs), 154, 38).large(true).recipeContext(this);
    }

    public static class Lens extends JewelryERecipe {

        protected Lens(List<EmiIngredient> inputs, List<EmiStack> outputs, RecipeEntry<? extends AbstractJewelryRecipe> recipeEntry) {
            super(inputs, outputs, recipeEntry);
        }

        public static Lens of(RecipeEntry<LensRecipe> entry) {
            List<EmiIngredient> input = Collections.singletonList(EmiStack.of(EItems.UNADJUSTED_LENS));
            List<EmiStack> output = Collections.singletonList(EmiStack.of(entry.value().getOutputItem()));
            return new Lens(input, output, entry);
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return EtherEMIPlugin.JEWELRY_LENS;
        }
    }

    public static class Modifier extends JewelryERecipe {

        protected Modifier(List<EmiIngredient> inputs, List<EmiStack> outputs, RecipeEntry<? extends AbstractJewelryRecipe> recipeEntry) {
            super(inputs, outputs, recipeEntry);
        }

        public static Modifier of(RecipeEntry<ModifierRecipe> entry) {
            LensModifier modifier = entry.value().getModifier();
            ComponentChanges changes = ComponentChanges.builder().add(ComponentTypes.LENS, LensComponent.EMPTY.incrementLevel(modifier)).build();
            List<EmiIngredient> input = Collections.singletonList(EmiIngredient.of(Arrays.stream(EItems.LENSES).map(EmiStack::of).toList()));
            List<EmiStack> output = Arrays.stream(EItems.LENSES).map(lensItem -> EmiStack.of(lensItem, changes)).toList();
            return new Modifier(input, output, entry);
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return EtherEMIPlugin.JEWELRY_MODIFIER;
        }
    }
}
