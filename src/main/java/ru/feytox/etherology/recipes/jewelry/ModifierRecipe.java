package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

@Getter
public class ModifierRecipe extends AbstractJewelryRecipe {

    private final LensModifier modifier;

    public ModifierRecipe(Pattern pattern, LensModifier modifier, int ether) {
        super(pattern, ether);
        this.modifier = modifier;
    }

    @Override
    protected boolean recipeMatches(ItemStack lensStack) {
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return false;
        if (lensItem.isUnadjusted()) return false;
        return super.recipeMatches(lensStack);
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        return craft(inventory);
    }

    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack lensStack = inventory.getStack(0);
        LensComponent.getWrapper(inventory.getStack(0))
                .ifPresent(data -> data.set(modifier, LensComponent::incrementLevel)
                        .set(LensPattern.empty(), LensComponent::withPattern).save());

        return lensStack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return ModifierRecipeSerializer.INSTANCE;
    }
}
