package ru.feytox.etherology.recipes.empower;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import ru.feytox.etherology.recipes.FeyRecipe;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

@Getter
@RequiredArgsConstructor
public class EmpowerRecipe implements FeyRecipe<ImplementedInventory> {

    private final DefaultedList<Ingredient> gridInput;
    private final int rellaCount;
    private final int viaCount;
    private final int closCount;
    private final int ketaCount;
    @Getter(value = AccessLevel.PRIVATE)
    private final ItemStack outputStack;
    private final Identifier id;

    @Override
    public boolean matches(ImplementedInventory inventory, World world) {
        if (inventory.size() != 10) return false;

        int invSlot = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i == 0 || i == 2) && (j == 0 || j == 2)) continue;

                Ingredient ingredient = this.gridInput.get(j + i * 3);
                if (!ingredient.test(inventory.getStack(invSlot))) {
                    return false;
                }

                invSlot++;
            }
        }

        return true;
    }

    public boolean checkShards(ImplementedInventory inventory) {
        return inventory.getStack(5).getCount() >= rellaCount
                && inventory.getStack(6).getCount() >= viaCount
                && inventory.getStack(7).getCount() >= closCount
                && inventory.getStack(8).getCount() >= ketaCount;
    }

    @Override
    public ItemStack craft(ImplementedInventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResult() {
        return outputStack.copy();
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return EmpowerRecipeSerializer.INSTANCE;
    }
}
