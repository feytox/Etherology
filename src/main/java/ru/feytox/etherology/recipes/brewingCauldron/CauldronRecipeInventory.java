package ru.feytox.etherology.recipes.brewingCauldron;

import lombok.Getter;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.magic.aspects.AspectContainer;

@Getter
public class CauldronRecipeInventory extends SimpleInventory {

    private final AspectContainer cauldronAspects;

    public CauldronRecipeInventory(AspectContainer cauldronAspects, ItemStack stack) {
        super(stack);
        this.cauldronAspects = cauldronAspects;
    }
}
