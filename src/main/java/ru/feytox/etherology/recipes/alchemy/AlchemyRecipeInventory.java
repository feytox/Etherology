package ru.feytox.etherology.recipes.alchemy;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import ru.feytox.etherology.magic.aspects.AspectContainer;

public record AlchemyRecipeInventory(AspectContainer cauldronAspects, ItemStack stack) implements RecipeInput {

    @Override
    public ItemStack getStackInSlot(int slot) {
        return stack;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
