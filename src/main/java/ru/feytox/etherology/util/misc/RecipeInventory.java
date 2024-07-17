package ru.feytox.etherology.util.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class RecipeInventory<T extends Inventory> implements RecipeInput {

    private final T inventory;

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public int getSize() {
        return inventory.size();
    }
}
