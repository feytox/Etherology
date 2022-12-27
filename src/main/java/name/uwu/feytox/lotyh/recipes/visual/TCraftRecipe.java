package name.uwu.feytox.lotyh.recipes.visual;

import name.uwu.feytox.lotyh.util.LIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCraftRecipe {
    Map<Integer, Ingredient> ingredients;
    ItemStack result;

    public TCraftRecipe(Map<Integer, Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public Map<Integer, LIngredient> getMatchingItems() {
        Map<Integer, LIngredient> matchingItems = new HashMap<>();
        this.ingredients.forEach((slot_num, ingredient) -> {
            List<ItemStack> matchingStacks = Arrays.stream(ingredient.getMatchingStacks()).toList();
            matchingItems.put(slot_num, new LIngredient(matchingStacks));
        });
        return matchingItems;
    }

    public ItemStack getResult() {
        return this.result;
    }
}


/*
123
456
789
 */
