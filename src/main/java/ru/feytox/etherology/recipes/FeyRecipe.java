package ru.feytox.etherology.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import ru.feytox.etherology.util.misc.RecipeInventory;

public interface FeyRecipe<T extends Inventory> extends FeyInputRecipe<RecipeInventory<T>> {


    @Override
    default boolean matches(RecipeInventory<T> input, World world) {
        return matches(input.getInventory(), world);
    }

    @Override
    default ItemStack craft(RecipeInventory<T> input, RegistryWrapper.WrapperLookup lookup) {
        return craft(input.getInventory(), lookup);
    }

    boolean matches(T inventory, World world);
    ItemStack craft(T inventory, RegistryWrapper.WrapperLookup lookup);
}
