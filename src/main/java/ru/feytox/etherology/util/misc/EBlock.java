package ru.feytox.etherology.util.misc;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.datagen.BlockLootTableGeneration;

public class EBlock {
    private final Block block;

    public EBlock(Block block) {
        this.block = block;
    }

    public Block withItem() {
        return withItem(true, null);
    }

    public Block withItem(boolean generateDrop) {
        return withItem(generateDrop, null);
    }

    public Block withItem(boolean generateDrop, @Nullable ItemConvertible drop) {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
        Registry.register(Registries.ITEM, Registries.BLOCK.getId(blockItem.getBlock()), blockItem);

        if (generateDrop) BlockLootTableGeneration.generateDrop(block, drop);
        return block;
    }

    public Block withoutItem() {
        return block;
    }
}
