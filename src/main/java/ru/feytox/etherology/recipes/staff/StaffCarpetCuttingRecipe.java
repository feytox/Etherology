package ru.feytox.etherology.recipes.staff;

import lombok.val;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.util.EtherologyComponents;

import static ru.feytox.etherology.recipes.staff.StaffCarpetingRecipe.getIndexesOfPair;
import static ru.feytox.etherology.registry.util.RecipesRegistry.STAFF_CARPET_CUT;

public class StaffCarpetCuttingRecipe extends SpecialCraftingRecipe {

    public StaffCarpetCuttingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        Pair<Integer, Integer> result = getIndexesOfStaffAndShears(inventory);
        if (result == null) return false;
        ItemStack staffStack = inventory.getStack(result.getRight());
        val staff = EtherologyComponents.STAFF.get(staffStack);
        val parts = staff.getParts();
        return parts.containsKey(StaffPart.HANDLE);
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        Pair<Integer, Integer> pair = getIndexesOfStaffAndShears(inventory);
        if (pair == null) {
            throw new NullPointerException("Could not find staff and/or shears");
        }

        ItemStack staffStack = inventory.getStack(pair.getLeft());
        ItemStack resultStack = staffStack.copy();

        val staff = EtherologyComponents.STAFF.get(resultStack);
        staff.removePartInfo(StaffPart.HANDLE);
        return resultStack;
    }

    @Nullable
    private static Pair<Integer, Integer> getIndexesOfStaffAndShears(CraftingInventory inventory) {
        return getIndexesOfPair(inventory, stack -> stack.isOf(ToolItems.STAFF), stack -> stack.getItem() instanceof ShearsItem);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        Pair<Integer, Integer> pair = getIndexesOfStaffAndShears(inventory);
        if (pair == null) return items;

        int shearsIndex = pair.getRight();
        ItemStack shearsStack = inventory.getStack(shearsIndex).copy();
        if (shearsStack.damage(1, Random.create(), null)) shearsStack.decrement(1);
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
