package ru.feytox.etherology.recipes.jewelry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.block.jewelryTable.JewelryTableInventory;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipe;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

@Getter
@RequiredArgsConstructor
public abstract class AbstractJewelryRecipe implements FeyRecipe<JewelryTableInventory> {

    private final LensPattern pattern;
    private final int ether;
    private final Identifier id;

    @Override
    public boolean matches(JewelryTableInventory inventory, World world) {
        // TODO: 11.06.2024 you know
        ItemStack lensStack = inventory.getStack(0);
        val lensOptional = EtherologyComponents.LENS.maybeGet(lensStack);
        return lensOptional.map(lensComponent -> lensComponent.getPattern().equals(pattern)).orElse(false);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }
}
