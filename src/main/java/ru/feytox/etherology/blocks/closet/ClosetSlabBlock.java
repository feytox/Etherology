package ru.feytox.etherology.blocks.closet;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import ru.feytox.etherology.enums.FurnitureType;
import ru.feytox.etherology.furniture.AbstractFurSlabBlock;

public class ClosetSlabBlock extends AbstractFurSlabBlock {
    public ClosetSlabBlock() {
        super("closet_slab", FabricBlockSettings.copyOf(Blocks.CHEST), FurnitureType.CLOSET);
    }
}
