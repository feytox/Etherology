package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

@Getter
public class LensRecipe extends AbstractJewelryRecipe {

    private final Item outputItem;

    public LensRecipe(LensPattern pattern, Item outputItem, int ether, Identifier id) {
        super(pattern, ether, id);
        this.outputItem = outputItem;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return outputItem.getDefaultStack();
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        return craft(inventory);
    }

    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack newLens = inventory.getStack(0).copyComponentsToNewStack(outputItem, 1);
        LensComponent.getWrapper(newLens).ifPresent(data -> data.set(LensPattern.empty(), LensComponent::withPattern).save());
        return newLens;
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return LensRecipeSerializer.INSTANCE;
    }
}
