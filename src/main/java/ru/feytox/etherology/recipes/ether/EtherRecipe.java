package ru.feytox.etherology.recipes.ether;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class EtherRecipe implements Recipe<ImplementedInventory> {
    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> gridInput;
    private final int relaCount;
    private final int viaCount;
    private final int closCount;
    private final int ketaCount;
    private final ItemStack output;
    private final Identifier id;

    public EtherRecipe(DefaultedList<Ingredient> gridInput, int relaCount, int viaCount, int closCount,
                       int ketaCount, ItemStack output, Identifier id, int width, int height) {
        this.gridInput = gridInput;
        this.relaCount = relaCount;
        this.viaCount = viaCount;
        this.closCount = closCount;
        this.ketaCount = ketaCount;
        this.output = output;
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public DefaultedList<Ingredient> gridInput() {
        return gridInput;
    }

    public int relaCount() {
        return relaCount;
    }

    public int viaCount() {
        return viaCount;
    }

    public int closCount() {
        return closCount;
    }

    public int ketaCount() {
        return ketaCount;
    }

    // TODO: 04/04/2023 finish deprecation
    @Deprecated
    public int width() {
        return width;
    }

    @Deprecated
    public int height() {
        return height;
    }

    @Override
    public boolean matches(ImplementedInventory inventory, World world) {
        if (inventory.size() != 10) return false;

        int invSlot = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i == 0 || i == 2) && (j == 0 || j == 2)) continue;

                Ingredient ingredient = this.gridInput.get(i + j * 3);
                if (!ingredient.test(inventory.getStack(invSlot))) {
                    return false;
                }

                invSlot++;
            }
        }

        return true;
    }

    public boolean checkShards(ImplementedInventory inventory) {
        return inventory.getStack(5).getCount() >= relaCount
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
    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EtherRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<EtherRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();

        public static final String ID = "ether_recipe";
    }
}
