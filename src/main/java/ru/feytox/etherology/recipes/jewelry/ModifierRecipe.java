package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

@Getter
public class ModifierRecipe extends AbstractJewelryRecipe {

    private final LensModifier modifier;

    public ModifierRecipe(LensPattern pattern, LensModifier modifier, int ether, Identifier id) {
        super(pattern, ether, id);
        this.modifier = modifier;
    }

    @Override
    public ItemStack craft(JewelryTableInventory inventory) {
        ItemStack lensStack = inventory.getStack(0);
        val lensData = EtherologyComponents.LENS.get(lensStack);
        val modifiers = lensData.getModifiers();
        modifiers.incrementLevel(modifier);
        lensData.setModifiers(modifiers);
        lensData.setPattern(LensPattern.empty());
        return lensStack;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return ModifierRecipeSerializer.INSTANCE;
    }
}
