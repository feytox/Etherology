package ru.feytox.etherology.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.compat.emi.EtherEMIPlugin;
import ru.feytox.etherology.compat.emi.misc.AspectStack;
import ru.feytox.etherology.compat.emi.misc.FeyEmiRecipe;
import ru.feytox.etherology.recipes.matrix.MatrixRecipe;

import java.util.Collections;
import java.util.List;

public class MatrixERecipe extends FeyEmiRecipe {

    private MatrixERecipe(List<EmiIngredient> inputs, List<EmiStack> outputs, Identifier id) {
        super(inputs, outputs, id);
    }

    public static MatrixERecipe of(RecipeEntry<MatrixRecipe> entry) {
        MatrixRecipe recipe = entry.value();
        List<EmiIngredient> inputs = ObjectArrayList.of(EmiIngredient.of(recipe.getCenterInput()));
        recipe.getAspects().forEach(aspect -> inputs.add(new AspectStack(aspect, 1)));
        List<EmiStack> output = Collections.singletonList(EmiStack.of(recipe.getOutput()));
        return new MatrixERecipe(inputs, output, entry.id());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EtherEMIPlugin.MATRIX;
    }

    @Override
    public int getDisplayWidth() {
        return Math.max(140, 140 + (inputs.size() - 4) * 18);
    }

    @Override
    public int getDisplayHeight() {
        return 44;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int dx = 1;
        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), dx, 14);
            if (i == 0) dx += 9;
            dx += 18;
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, dx+4, 15);
        widgets.addSlot(outputs.getFirst(), dx+31, 10).large(true).recipeContext(this);
    }
}
