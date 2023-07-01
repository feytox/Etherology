package ru.feytox.etherology.recipes.brewingCauldron;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.aspects.EtherAspect;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;

import java.util.Map;

@RequiredArgsConstructor
public class CauldronRecipe implements Recipe<CauldronRecipeInventory> {
    @Getter
    private final Ingredient inputItem;
    @Getter
    private final int inputAmount;
    @Getter
    private final EtherAspectsContainer inputAspects;
    private final ItemStack outputStack;
    private final Identifier id;

    @Override
    public boolean matches(CauldronRecipeInventory inventory, World world) {
        DefaultedList<ItemStack> stacks = inventory.stacks;
        if (stacks.size() != 1 || !inputItem.test(stacks.get(0)) || stacks.get(0).getCount() < inputAmount) return false;

        ImmutableMap<EtherAspect, Integer> cauldronAspects = inventory.getCauldronAspects().getAspects();
        for (Map.Entry<EtherAspect, Integer> inputEntry : inputAspects.getAspects().entrySet()) {
            Integer cauldronValue = cauldronAspects.get(inputEntry.getKey());
            if (cauldronValue == null || cauldronValue < inputEntry.getValue()) return false;
        }

        return true;
    }

    @Override
    public ItemStack craft(CauldronRecipeInventory inventory) {
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
        return CauldronRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CauldronRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "brewing_cauldron_recipe";
    }
}
