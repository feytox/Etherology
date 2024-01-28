package ru.feytox.etherology.recipes.armillary;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.enums.InstabilityType;
import ru.feytox.etherology.recipes.FeyRecipe;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ArmillaryRecipe implements FeyRecipe<Inventory> {

    private final List<Ingredient> inputs;
    private final Ingredient centerInput;
    private final InstabilityType instability;
    private final float etherPoints;
    private final ItemStack outputStack;
    private final Identifier id;

    public ArmillaryRecipe(List<Ingredient> inputs, Ingredient centerInput, String instability,
                           float etherPoints, ItemStack outputStack, Identifier id) {
        this(inputs, centerInput, InstabilityType.valueOf(instability), etherPoints, outputStack, id);
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
    public FeyRecipeSerializer<?> getSerializer() {
        return ArmillaryRecipeSerializer.INSTANCE;
    }
}
