package name.uwu.feytox.etherology.recipes.visual;

import name.uwu.feytox.etherology.enums.MixTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Map;

public class TAlchemyRecipe extends TCraftRecipe {
    Map<Integer, MixTypes> mixes;

    public TAlchemyRecipe(Map<Integer, Ingredient> ingredients, Map<Integer, MixTypes> mixes, ItemStack result) {
        super(ingredients, result);
        this.mixes = mixes;
    }

    public Map<Integer, MixTypes> getMixes() {
        return mixes;
    }

    public int size() {
        return this.ingredients.size() + this.mixes.size();
    }
}
