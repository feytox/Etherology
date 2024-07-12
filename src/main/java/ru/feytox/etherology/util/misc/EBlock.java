package ru.feytox.etherology.util.misc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.block.AutoBlockLootTable;

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
        Items.register(block);

        if (generateDrop) AutoBlockLootTable.markAsAuto(block, drop);
        return block;
    }

    public Block withoutItem() {
        return block;
    }
}
