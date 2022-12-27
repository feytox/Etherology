package name.uwu.feytox.lotyh.recipes.armillary;

import name.uwu.feytox.lotyh.enums.InstabTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ArmillaryRecipe implements Recipe<Inventory> {
    private final List<Ingredient> inputs;
    private final Ingredient centerInput;
    private final float etherPoints;
    private final ItemStack outputStack;
    private final Identifier id;

    public ArmillaryRecipe(List<Ingredient> inputs, Ingredient centerInput, float etherPoints, ItemStack outputStack, Identifier id) {
        this.inputs = inputs;
        this.centerInput = centerInput;
        this.etherPoints = etherPoints;
        this.outputStack = outputStack;
        this.id = id;
    }

    public Ingredient getCenterInput() {
        return centerInput;
    }


    public InstabTypes getInstability() {
        // TODO: add instability to recipe
        int instability = 0;
        return InstabTypes.getFromIndex(instability);
    }

    public float getEtherPoints() {
        return etherPoints;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        List<Ingredient> ingredients = new ArrayList<>(this.inputs);
        int invSize = inventory.size();

        if (invSize < ingredients.size() + 1) return false;

        for (int i = 0; i < invSize-1; i++) {
            ItemStack itemStack = inventory.getStack(i);
            int searchResult = -1;
            for (int j = 0; j < ingredients.size(); j++) {
                if (ingredients.get(j).test(itemStack)) {
                    searchResult = j;
                    break;
                }
            }
            if (searchResult != -1) {
                ingredients.remove(searchResult);
            }
        }

        if (!ingredients.isEmpty()) return false;

        return centerInput.test(inventory.getStack(invSize-1));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return outputStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ArmillaryRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ArmillaryRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();

        public static final String ID = "armillary_recipe";
    }
}
