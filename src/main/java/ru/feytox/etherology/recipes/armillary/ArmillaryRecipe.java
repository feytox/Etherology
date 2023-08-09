package ru.feytox.etherology.recipes.armillary;

import lombok.Getter;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.enums.InstabilityType;

import java.util.ArrayList;
import java.util.List;

public class ArmillaryRecipe implements Recipe<Inventory> {
    @Getter
    private final List<Ingredient> inputs;
    @Getter
    private final Ingredient centerInput;
    @Getter
    private final InstabilityType instability;
    @Getter
    private final float etherPoints;
    private final ItemStack outputStack;
    private final Identifier id;

    public ArmillaryRecipe(List<Ingredient> inputs, Ingredient centerInput, String instability,
                           float etherPoints, ItemStack outputStack, Identifier id) {
        this.inputs = inputs;
        this.centerInput = centerInput;
        this.instability = InstabilityType.valueOf(instability);
        this.etherPoints = etherPoints;
        this.outputStack = outputStack;
        this.id = id;
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
        return outputStack.copy();
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
