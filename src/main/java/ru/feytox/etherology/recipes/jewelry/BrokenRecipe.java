package ru.feytox.etherology.recipes.jewelry;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.block.jewelryTable.JewelryBlockEntity;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.util.misc.EIdentifier;

public class BrokenRecipe extends AbstractJewelryRecipe {

    public static final BrokenRecipe INSTANCE = new BrokenRecipe();

    public BrokenRecipe() {
        super(LensPattern.empty(), 6, EIdentifier.of("broken_lens_jewelry"));
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        return craft(inventory);
    }

    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack lensStack = inventory.getStack(0);
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return lensStack;

        JewelryBlockEntity parent = inventory.getParent();
        if (parent == null) return lensStack;

        // stopship: continue
        LensComponent.getWrapper(lensStack).filter(data -> data.getComponent().pattern().isCracked())
                .ifPresent(data -> {
                    data.set(LensPattern.empty(), LensComponent::withPattern).save();
                    parent.applyCorruption();
                    if (!LensItem.damageLens(lensStack, 5)) return;
                    JewelryTableInventory.onLensDamage(parent, lensStack, lensItem);
                    parent.trySyncData();
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
