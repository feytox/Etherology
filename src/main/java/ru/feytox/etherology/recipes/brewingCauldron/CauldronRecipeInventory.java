package ru.feytox.etherology.recipes.brewingCauldron;

import lombok.Getter;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;

public class CauldronRecipeInventory extends SimpleInventory {
    @Getter
    private final EtherAspectsContainer cauldronAspects;

    public CauldronRecipeInventory(EtherAspectsContainer cauldronAspects, ItemStack stack) {
        super(stack);
        this.cauldronAspects = cauldronAspects;
    }
}
