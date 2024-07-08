package ru.feytox.etherology.block.closet;

import net.minecraft.block.Blocks;
import ru.feytox.etherology.block.furniture.AbstractFurSlabBlock;
import ru.feytox.etherology.enums.FurnitureType;

public class ClosetSlabBlock extends AbstractFurSlabBlock {
    public ClosetSlabBlock() {
        super("closet_slab", Settings.copy(Blocks.CHEST), FurnitureType.CLOSET);
    }
}
