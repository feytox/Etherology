package name.uwu.feytox.lotyh.recipes;

import name.uwu.feytox.lotyh.recipes.visual.TCraftRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class BaseRecipe {
    Map<Integer, Ingredient> ingredients = new HashMap<>();
    ItemStack result = Items.AIR.getDefaultStack();

    public BaseRecipe(Map<Integer, Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public BaseRecipe() {
    }

    public BaseRecipe addIngredient(Ingredient ingredient, int slotNum) {
        this.ingredients.put(slotNum, ingredient);
        return this;
    }

    public BaseRecipe addIngredient(Ingredient ingredient) {
        if (!this.ingredients.isEmpty()) {
            return this.addIngredient(ingredient, this.ingredients.size());
        }
        return this.addIngredient(ingredient, 0);
    }

    public BaseRecipe addIngredient(ItemStack itemStack) {
        return this.addIngredient(Ingredient.ofStacks(itemStack));
    }

    public BaseRecipe addIngredient(ItemStack itemStack, int slotNum) {
        return this.addIngredient(Ingredient.ofStacks(itemStack), slotNum);
    }

    public boolean isMatch(BaseRecipe secondRecipe) {
        return this.ingredients == secondRecipe.ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public Map<Integer, Ingredient> getIngredients() {
        return ingredients;
    }
}
