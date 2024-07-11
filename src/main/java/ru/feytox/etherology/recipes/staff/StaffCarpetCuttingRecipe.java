package ru.feytox.etherology.recipes.staff;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.item.ToolItems;

import static ru.feytox.etherology.recipes.staff.StaffCarpetingRecipe.getIndexesOfPair;
import static ru.feytox.etherology.registry.misc.RecipesRegistry.STAFF_CARPET_CUT;

public class StaffCarpetCuttingRecipe extends SpecialCraftingRecipe {

    public StaffCarpetCuttingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        Pair<Integer, Integer> result = getIndexesOfStaffAndShears(inventory);
        if (result == null) return false;
        ItemStack staffStack = inventory.getStack(result.getLeft());

        return StaffComponent.get(staffStack).map(StaffComponent::parts)
                .map(parts -> parts.containsKey(StaffPart.HANDLE)).orElse(false);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        Pair<Integer, Integer> pair = getIndexesOfStaffAndShears(inventory);
        if (pair == null) {
            throw new NullPointerException("Could not find staff and/or shears");
        }

        ItemStack staffStack = inventory.getStack(pair.getLeft());
        ItemStack resultStack = staffStack.copy();

        StaffComponent.getWrapper(resultStack).ifPresent(data ->
                data.set(StaffPart.HANDLE, StaffComponent::removePartInfo).save());
        return resultStack;
    }

    @Nullable
    private static Pair<Integer, Integer> getIndexesOfStaffAndShears(RecipeInputInventory inventory) {
        return getIndexesOfPair(inventory, stack -> stack.isOf(ToolItems.STAFF), stack -> stack.getItem() instanceof ShearsItem);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        Pair<Integer, Integer> pair = getIndexesOfStaffAndShears(inventory);
        if (pair == null) return items;

        int shearsIndex = pair.getRight();
        ItemStack shearsStack = inventory.getStack(shearsIndex).copy();
        shearsStack.damage(1, Random.create(), null, () -> shearsStack.decrement(1));
        items.set(shearsIndex, shearsStack);
        return items;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return STAFF_CARPET_CUT;
    }
}
