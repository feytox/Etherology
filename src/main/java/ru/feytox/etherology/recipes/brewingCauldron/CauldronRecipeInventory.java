package ru.feytox.etherology.recipes.brewingCauldron;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import ru.feytox.etherology.magic.aspects.AspectContainer;

public record CauldronRecipeInventory(AspectContainer cauldronAspects, ItemStack stack) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
