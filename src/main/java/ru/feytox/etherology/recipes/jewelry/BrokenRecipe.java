package ru.feytox.etherology.recipes.jewelry;

import lombok.val;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.block.jewelryTable.JewelryBlockEntity;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;

public class BrokenRecipe extends AbstractJewelryRecipe {

    public static final BrokenRecipe INSTANCE = new BrokenRecipe();

    public BrokenRecipe() {
        super(LensPattern.empty(), 6, new EIdentifier("broken_lens_jewelry"));
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack lensStack = inventory.getStack(0);
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return lensStack;

        val lensOptional = EtherologyComponents.LENS.maybeGet(lensStack);
        if (lensOptional.isEmpty()) return lensStack;

        val lensData = lensOptional.get();
        LensPattern pattern = lensData.getPattern();
        if (!pattern.isCracked()) return lensStack;

        JewelryBlockEntity parent = inventory.getParent();
        if (parent == null) return lensStack;

        parent.applyCorruption();
        lensData.setPattern(LensPattern.empty());
        if (!LensItem.damageLens(lensStack, 5)) return lensStack;
        JewelryTableInventory.onLensDamage(parent, lensStack, lensItem);
        parent.trySyncData();
        return lensStack;
    }

    @Override
    protected boolean recipeMatches(ItemStack lensStack) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return BrokenRecipeSerializer.INSTANCE;
    }
}
