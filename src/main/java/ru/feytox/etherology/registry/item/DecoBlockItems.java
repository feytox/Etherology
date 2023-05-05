package ru.feytox.etherology.registry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class DecoBlockItems {
    // peach wood
    public static final Item PEACH_DOOR = registerBlockItem(new TallBlockItem(DecoBlocks.PEACH_DOOR, new FabricItemSettings()));
    public static final Item PEACH_SIGN = registerBlockItem(new SignItem(new FabricItemSettings().maxCount(16), DecoBlocks.PEACH_SIGN, DecoBlocks.PEACH_WALL_SIGN));

    // plants
    public static final Item BEAMER_SEEDS = registerAliasedBlockItem("beamer_seeds", DecoBlocks.BEAMER);
    public static final Item BEAM_FRUIT = registerAliasedBlockItem("beam_fruit", DecoBlocks.BEAMER);

    // metals
    public static final Item ATTRAHITE_INGOT = registerSimpleItem("attrahite_ingot");
    public static final Item ATTRAHITE_NUGGET = registerSimpleItem("attrahite_nugget");
    public static final Item ETHRIL_INGOT = registerSimpleItem("ethril_ingot");
    public static final Item ETHRIL_NUGGET = registerSimpleItem("ethril_nugget");
    public static final Item TELDER_STEEL_INGOT = registerSimpleItem("telder_steel_ingot");
    public static final Item TELDER_STEEL_NUGGET = registerSimpleItem("telder_steel_nugget");

    private static Item registerSimpleItem(String id) {
        return Registry.register(Registries.ITEM, new EIdentifier(id), new Item(new FabricItemSettings()));
    }

    private static Item registerBlockItem(BlockItem blockItem) {
        blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
        return Registry.register(Registries.ITEM, Registries.BLOCK.getId(blockItem.getBlock()), blockItem);
    }

    private static Item registerBlockItem(Block block) {
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        return registerBlockItem(blockItem);
    }

    public static Item registerAliasedBlockItem(String id, Block block) {
        AliasedBlockItem aliasedBlockItem = new AliasedBlockItem(block, new FabricItemSettings());
        Registry.register(Registries.ITEM, new EIdentifier(id), aliasedBlockItem);
        return aliasedBlockItem;
    }

    public static void registerAll() {}
}
