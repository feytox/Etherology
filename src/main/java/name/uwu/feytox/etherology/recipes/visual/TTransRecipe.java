package name.uwu.feytox.etherology.recipes.visual;

import name.uwu.feytox.etherology.enums.InstabTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Map;

public class TTransRecipe extends TCraftRecipe {
    private final InstabTypes instabLevel;
    private final int energyLevel;

    public TTransRecipe(Map<Integer, Ingredient> ingredients, InstabTypes unstabLevel, int energyLevel, ItemStack result) {
        super(ingredients, result);
        this.instabLevel = unstabLevel;
        this.energyLevel = energyLevel;
    }

    public InstabTypes getInstabLevel() {
        return instabLevel;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }
}
