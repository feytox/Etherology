package ru.feytox.etherology.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;

public interface FeyRecipe<T extends RecipeInput> extends Recipe<T> {

    @Override
    FeyRecipeSerializer<?> getSerializer();

    @Override
    default RecipeType<?> getType() {
        return getSerializer().getRecipeType();
    }
}
