package ru.feytox.etherology;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class DecoBlockItems {
    public static final Item PEACH_DOOR = registerBlockItem(new TallBlockItem(DecoBlocks.PEACH_DOOR, new FabricItemSettings()));
    public static final Item PEACH_SIGN = registerBlockItem(new SignItem(new FabricItemSettings().maxCount(16), DecoBlocks.PEACH_SIGN, DecoBlocks.PEACH_WALL_SIGN));

    private static Item registerBlockItem(BlockItem blockItem) {
        blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
        return Registry.register(Registries.ITEM, Registries.BLOCK.getId(blockItem.getBlock()), blockItem);
    }

    private static Item registerBlockItem(Block block) {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        return registerBlockItem(blockItem);
    }

    public static void registerAll() {}
}
