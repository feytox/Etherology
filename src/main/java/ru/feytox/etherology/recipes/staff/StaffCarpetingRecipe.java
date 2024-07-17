package ru.feytox.etherology.recipes.staff;

import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.staff.*;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.RecipesRegistry;

import java.util.function.Predicate;

public class StaffCarpetingRecipe extends SpecialCraftingRecipe {

    public StaffCarpetingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput inventory, World world) {
        Pair<Integer, Integer> result = getIndexesOfStaffAndCarpet(inventory);
        return result != null;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        Pair<Integer, Integer> pair = getIndexesOfStaffAndCarpet(inventory);
        if (pair == null) {
            throw new NullPointerException("Could not find staff and/or carpet");
        }

        ItemStack staffStack = inventory.getStackInSlot(pair.getLeft());
        ItemStack resultStack = staffStack.copy();
        ItemStack carpetStack = inventory.getStackInSlot(pair.getRight());

        if (!(carpetStack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof DyedCarpetBlock carpet)) {
            throw new NullPointerException("Could not find carpet");
        }

        String carpetColorName = carpet.getDyeColor().asString();
        StaffColors carpetColor = StaffColors.getFromColorName(carpetColorName);
        if (carpetColor == null) {
            throw new NullPointerException("Could not find StaffColor for carpet color '" + carpetColorName + "'");
        }

        StaffComponent.getWrapper(resultStack).ifPresent(data ->
                data.set(new StaffPartInfo(StaffPart.HANDLE, carpetColor, StaffPattern.EMPTY), StaffComponent::setPartInfo).save());
        return resultStack;
    }

    @Nullable
    private static Pair<Integer, Integer> getIndexesOfStaffAndCarpet(CraftingRecipeInput inventory) {
        return getIndexesOfPair(inventory, stack -> stack.isOf(ToolItems.STAFF), stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DyedCarpetBlock);
    }

    @Nullable
    public static Pair<Integer, Integer> getIndexesOfPair(CraftingRecipeInput inventory, Predicate<ItemStack> leftPredicate, Predicate<ItemStack> rightPredicate) {
        int leftCount = 0;
        int rightCount = 0;
        int leftIndex = -1;
        int rightIndex = -1;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (leftPredicate.test(stack)) {
                leftCount++;
                leftIndex = i;
            }
            else {
                if (!rightPredicate.test(stack)) return null;
                rightCount++;
                rightIndex = i;
            }

            if (rightCount > 1 || leftCount > 1) return null;
        }

        if (rightCount != 1 || leftCount != 1) return null;
        return new Pair<>(leftIndex, rightIndex);
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
