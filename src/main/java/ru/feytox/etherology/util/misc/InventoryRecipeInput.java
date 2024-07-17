package ru.feytox.etherology.util.misc;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public interface InventoryRecipeInput extends ImplementedInventory, RecipeInput {

    @Override
    default boolean isEmpty() {
        return ImplementedInventory.super.isEmpty();
    }

    @Override
    default int getSize() {
        return ImplementedInventory.super.size();
    }

    @Override
    default ItemStack getStackInSlot(int slot) {
        return ImplementedInventory.super.getStack(slot);
    }
}
