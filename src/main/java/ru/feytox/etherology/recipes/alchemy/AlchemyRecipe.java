package ru.feytox.etherology.recipes.alchemy;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.recipes.FeyInputRecipe;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.Map;

@RequiredArgsConstructor
public class AlchemyRecipe implements FeyInputRecipe<AlchemyRecipeInventory> {

    @Getter
    private final Ingredient inputItem;
    @Getter
    private final int inputAmount;
    @Getter
    private final AspectContainer inputAspects;
    private final ItemStack outputStack;

    @Override
    public boolean matches(AlchemyRecipeInventory inventory, World world) {
        if (!inputItem.test(inventory.stack()) || inventory.stack().getCount() < inputAmount) return false;

        ImmutableMap<Aspect, Integer> cauldronAspects = inventory.cauldronAspects().getAspects();
        for (Map.Entry<Aspect, Integer> inputEntry : inputAspects.getAspects().entrySet()) {
            Integer cauldronValue = cauldronAspects.get(inputEntry.getKey());
            if (cauldronValue == null || cauldronValue < inputEntry.getValue()) return false;
        }

        return true;
    }

    @Override
    public ItemStack craft(AlchemyRecipeInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return getOutput();
    }

    public ItemStack getOutput() {
        return outputStack.copy();
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return AlchemyRecipeSerializer.INSTANCE;
    }
}
