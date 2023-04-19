package ru.feytox.etherology;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class DecoBlockItems {
    public static final Item PEACH_DOOR = registerBlockItem(new TallBlockItem(DecoBlocks.PEACH_DOOR, new FabricItemSettings()));
    public static final Item PEACH_SIGN = registerBlockItem(new SignItem(new FabricItemSettings().maxCount(16), DecoBlocks.PEACH_SIGN, DecoBlocks.PEACH_WALL_SIGN));
    public static final Item BEAMER_SEEDS = registerAliasedBlockItem("beamer_seeds", DecoBlocks.BEAMER);
    public static final Item BEAMER_FRUIT = registerAliasedBlockItem("beamer_fruit", DecoBlocks.BEAMER);

    private static Item registerBlockItem(BlockItem blockItem) {
        blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
        return Registry.register(Registries.ITEM, Registries.BLOCK.getId(blockItem.getBlock()), blockItem);
    }

    private static Item registerBlockItem(Block block) {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        return registerBlockItem(blockItem);
    }

    private static Item registerAliasedBlockItem(String id, Block block) {
        AliasedBlockItem aliasedBlockItem = new AliasedBlockItem(block, new FabricItemSettings());
        Registry.register(Registries.ITEM, new EIdentifier(id), aliasedBlockItem);
        return aliasedBlockItem;
    }

    public static void registerAll() {}
}
