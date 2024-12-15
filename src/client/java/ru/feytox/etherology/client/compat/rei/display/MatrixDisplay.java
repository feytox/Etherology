package ru.feytox.etherology.client.compat.rei.display;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.client.compat.rei.misc.AspectPair;
import ru.feytox.etherology.recipes.matrix.MatrixRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MatrixDisplay extends BasicDisplay {

    public MatrixDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Identifier recipeId) {
        super(inputs, outputs, Optional.of(recipeId));
    }

    public static MatrixDisplay of(RecipeEntry<MatrixRecipe> entry) {
        MatrixRecipe recipe = entry.value();
        List<EntryIngredient> inputs = ObjectArrayList.of(EntryIngredients.ofIngredient(recipe.getCenterInput()));
        recipe.getAspects().forEach(aspect -> inputs.add(EntryIngredient.of(AspectPair.entry(aspect, 1))));
        List<EntryIngredient> output = Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
        return new MatrixDisplay(inputs, output, entry.id());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EtherREIPlugin.MATRIX;
    }
}
