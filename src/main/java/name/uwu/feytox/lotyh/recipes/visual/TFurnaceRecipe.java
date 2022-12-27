package name.uwu.feytox.lotyh.recipes.visual;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class TFurnaceRecipe extends TCraftRecipe {
    int ticks;

    private TFurnaceRecipe(Map<Integer, Ingredient> ingredients, int ticks, ItemStack result) {
        super(ingredients, result);
        this.ticks = ticks;
    }

    public TFurnaceRecipe(Ingredient ingredient, int ticks, ItemStack result) {
        this(Map.of(1, ingredient), ticks, result);
    }

    public TFurnaceRecipe(ItemStack itemStack, int ticks, ItemStack result) {
        this(Ingredient.ofStacks(itemStack), ticks, result);
    }

    public int getTicks() {
        return this.ticks;
    }
}
