package name.uwu.feytox.lotyh.recipes.alchemy;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlchemyRecipe implements Recipe<ImplementedInventory> {
    private final List<Ingredient> inputs;
    private final ItemStack outputStack;
    private final Identifier id;

    public AlchemyRecipe(List<Ingredient> inputs, ItemStack outputStack, Identifier id) {
        this.inputs = inputs;
        this.outputStack = outputStack;
        this.id = id;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public boolean matches(ImplementedInventory inventory, World world) {
        List<ItemStack> onlyItems = new ArrayList<>();
        inventory.getItems().forEach(itemStack -> {
            if (!itemStack.isEmpty()) onlyItems.add(itemStack);
        });

        if (onlyItems.size() != this.inputs.size()) {
            return false;
        }

        for (int i = 0; i < onlyItems.size(); i++) {
            Ingredient ingredient = this.inputs.get(i);
            ItemStack itemStack = onlyItems.get(i);
            if (!ingredient.test(itemStack))
                return false;
        }
        return true;
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
        return outputStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AlchemyRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AlchemyRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "alchemy_recipe";
    }

    private static List<ItemStack> subSort(List<ItemStack> list, int first, int last) {
        Collections.sort(list.subList(first, last), Comparator.comparing(ItemStack::getTranslationKey));
        return list;
    }
}
