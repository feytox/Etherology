package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import lombok.val;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

@Getter
public class LensRecipe extends AbstractJewelryRecipe {

    private final Item outputItem;

    public LensRecipe(LensPattern pattern, Item outputItem, int ether, Identifier id) {
        super(pattern, ether, id);
        this.outputItem = outputItem;
    }

    @Override
    public ItemStack getOutput() {
        return outputItem.getDefaultStack();
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack newLens = getOutput();
        newLens.setNbt(inventory.getStack(0).getNbt());
        val lens = EtherologyComponents.LENS.get(newLens);
        lens.setPattern(LensPattern.empty());
        return newLens;
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return LensRecipeSerializer.INSTANCE;
    }
}
