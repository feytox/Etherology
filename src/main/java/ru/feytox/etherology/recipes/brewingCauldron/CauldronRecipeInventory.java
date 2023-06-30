package ru.feytox.etherology.recipes.brewingCauldron;

import lombok.Getter;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.data.item_aspects.ItemAspectsContainer;

public class CauldronRecipeInventory extends SimpleInventory {
    @Getter
    private final ItemAspectsContainer cauldronAspects;

    public CauldronRecipeInventory(ItemAspectsContainer cauldronAspects, ItemStack stack) {
        super(stack);
        this.cauldronAspects = cauldronAspects;
    }
}
