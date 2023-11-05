package ru.feytox.etherology.recipes.staff;

import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.staff.StaffColors;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPattern;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.util.RecipesRegistry;

public class StaffCarpetingRecipe extends SpecialCraftingRecipe {
    public StaffCarpetingRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        Pair<Integer, Integer> result = getIndexesOfStaffAndCarpet(inventory);
        return result != null;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        Pair<Integer, Integer> pair = getIndexesOfStaffAndCarpet(inventory);
        if (pair == null) {
            throw new NullPointerException("Could not find staff and/or carpet");
        }

        ItemStack staffStack = inventory.getStack(pair.getLeft());
        ItemStack resultStack = staffStack.copy();
        ItemStack carpetStack = inventory.getStack(pair.getRight());

        if (!(carpetStack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof DyedCarpetBlock carpet)) {
            throw new NullPointerException("Could not find carpet");
        }

        String carpetColorName = carpet.getDyeColor().asString();
        StaffColors carpetColor = StaffColors.getFromColorName(carpetColorName);
        if (carpetColor == null) {
            throw new NullPointerException("Could not find StaffColor for carpet color '" + carpetColorName + "'");
        }

        StaffItem.setPartInfo(resultStack, StaffPart.HANDLE, carpetColor, StaffPattern.EMPTY);
        return resultStack;
    }
    
    @Nullable
    private static Pair<Integer, Integer> getIndexesOfStaffAndCarpet(CraftingInventory inventory) {
        int staffCount = 0;
        int carpetCount = 0;
        int staffIndex = -1;
        int carpetIndex = -1;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.isOf(ToolItems.STAFF)) {
                staffCount++;
                staffIndex = i;
            }
            else {
                if (!(stack.getItem() instanceof BlockItem blockItem)) return null;
                if (!(blockItem.getBlock() instanceof DyedCarpetBlock)) return null;
                carpetCount++;
                carpetIndex = i;
            }

            if (carpetCount > 1 || staffCount > 1) return null;
        }

        if (carpetCount != 1 || staffCount != 1) return null;
        return new Pair<>(staffIndex, carpetIndex);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipesRegistry.STAFF_CARPET;
    }
}
