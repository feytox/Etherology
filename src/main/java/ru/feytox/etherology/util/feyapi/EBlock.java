package ru.feytox.etherology.util.feyapi;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EBlock {
    private final Block block;

    public EBlock(Block block) {
        this.block = block;
    }

    public Block withItem() {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
        Registry.register(Registries.ITEM, Registries.BLOCK.getId(blockItem.getBlock()), blockItem);
        return block;
    }

    public Block withoutItem() {
        return block;
    }
}
