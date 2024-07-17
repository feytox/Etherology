package ru.feytox.etherology.recipes.jewelry;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.block.jewelryTable.JewelryBlockEntity;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Optional;

public class BrokenRecipe extends AbstractJewelryRecipe {

    public static final RecipeEntry<BrokenRecipe> INSTANCE = new RecipeEntry<>(EIdentifier.of("broken_lens_jewelry"), new BrokenRecipe());

    private BrokenRecipe() {
        super(new Pattern(LensPattern.empty(), Optional.empty()), 6);
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack lensStack = inventory.getStack(0);
        if (!(lensStack.getItem() instanceof LensItem)) return lensStack;

        JewelryBlockEntity parent = inventory.getParent();
        if (parent == null) return lensStack;

        LensComponent.getWrapper(lensStack).filter(data -> data.getComponent().pattern().isCracked())
                .ifPresent(data -> {
                    data.set(LensPattern.empty(), LensComponent::withPattern).save();
                    parent.applyCorruption();
                    inventory.damageLens(lensStack, 5);
                });

        return lensStack;
    }

    @Override
    protected boolean recipeMatches(ItemStack lensStack) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return BrokenRecipeSerializer.INSTANCE;
    }
}
