package ru.feytox.etherology.recipes.brewingCauldron;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.recipes.FeyRecipe;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.Map;

@RequiredArgsConstructor
public class CauldronRecipe implements FeyRecipe<CauldronRecipeInventory> {
    @Getter
    private final Ingredient inputItem;
    @Getter
    private final int inputAmount;
    @Getter
    private final AspectContainer inputAspects;
    private final ItemStack outputStack;
    @Getter
    private final Identifier id;

    @Override
    public boolean matches(CauldronRecipeInventory inventory, World world) {
        DefaultedList<ItemStack> stacks = inventory.heldStacks;
        if (stacks.size() != 1 || !inputItem.test(stacks.get(0)) || stacks.get(0).getCount() < inputAmount) return false;

        ImmutableMap<Aspect, Integer> cauldronAspects = inventory.getCauldronAspects().getAspects();
        for (Map.Entry<Aspect, Integer> inputEntry : inputAspects.getAspects().entrySet()) {
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
    public ItemStack getResult() {
        return outputStack.copy();
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return CauldronRecipeSerializer.INSTANCE;
    }
}
