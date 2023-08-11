package ru.feytox.etherology.block.closet;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import ru.feytox.etherology.block.furniture.AbstractFurSlabBlock;
import ru.feytox.etherology.enums.FurnitureType;

public class ClosetSlabBlock extends AbstractFurSlabBlock {
    public ClosetSlabBlock() {
        super("closet_slab", FabricBlockSettings.copyOf(Blocks.CHEST), FurnitureType.CLOSET);
    }
}
