package ru.feytox.etherology.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface FeyRecipe<T extends Inventory> extends Recipe<T> {

    @Override
    FeyRecipeSerializer<?> getSerializer();

    @Override
    default RecipeType<?> getType() {
        return getSerializer().getRecipeType();
    }
}
