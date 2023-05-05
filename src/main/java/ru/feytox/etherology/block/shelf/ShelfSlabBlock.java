package ru.feytox.etherology.block.shelf;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import ru.feytox.etherology.enums.FurnitureType;
import ru.feytox.etherology.furniture.AbstractFurSlabBlock;

public class ShelfSlabBlock extends AbstractFurSlabBlock {
    public ShelfSlabBlock() {
        super("shelf_slab", FabricBlockSettings.copyOf(Blocks.CHEST), FurnitureType.SHELF);
    }
}
