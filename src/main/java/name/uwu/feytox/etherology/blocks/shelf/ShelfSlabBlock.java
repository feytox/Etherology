package name.uwu.feytox.etherology.blocks.shelf;

import name.uwu.feytox.etherology.enums.FurnitureType;
import name.uwu.feytox.etherology.furniture.AbstractFurSlabBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;

public class ShelfSlabBlock extends AbstractFurSlabBlock {
    public ShelfSlabBlock() {
        super("shelf_slab", FabricBlockSettings.copyOf(Blocks.CHEST), FurnitureType.SHELF);
    }
}
