package name.uwu.feytox.etherology.blocks.closet;

import name.uwu.feytox.etherology.enums.FurnitureType;
import name.uwu.feytox.etherology.furniture.AbstractFurSlabBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;

public class ClosetSlabBlock extends AbstractFurSlabBlock {
    public ClosetSlabBlock() {
        super("closet_slab", FabricBlockSettings.copyOf(Blocks.CHEST), FurnitureType.CLOSET);
    }
}
