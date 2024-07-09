package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipe;

@Getter
@RequiredArgsConstructor
public abstract class AbstractJewelryRecipe implements FeyRecipe<JewelryTableInventory> {

    private final LensPattern pattern;
    private final int ether;
    private final Identifier id;

    @Override
    public boolean matches(JewelryTableInventory inventory, World world) {
        return recipeMatches(inventory.getStack(0));
    }

    protected boolean recipeMatches(ItemStack lensStack) {
        return LensComponent.get(lensStack)
                .map(component -> component.pattern().equals(pattern)).orElse(false);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }
}
