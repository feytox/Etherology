package ru.feytox.etherology.block.shelf;

import net.minecraft.block.Blocks;
import ru.feytox.etherology.block.furniture.AbstractFurSlabBlock;
import ru.feytox.etherology.enums.FurnitureType;

public class ShelfSlabBlock extends AbstractFurSlabBlock {
    public ShelfSlabBlock() {
        super("shelf_slab", Settings.copy(Blocks.CHEST), FurnitureType.SHELF);
    }
}
