package ru.feytox.etherology.registry.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.util.misc.EIdentifier;

// TODO: 16.06.2023 rename
// TODO: 29.02.2024 move to EItems
public class DecoBlockItems {
    // peach wood
    public static final Item PEACH_DOOR = registerBlockItem(new TallBlockItem(DecoBlocks.PEACH_DOOR, new Settings()));
    public static final Item PEACH_SIGN = registerBlockItem(new SignItem(new Settings().maxCount(16), DecoBlocks.PEACH_SIGN, DecoBlocks.PEACH_WALL_SIGN));

    // plants
    public static final Item BEAMER_SEEDS = registerAliasedBlockItem("beamer_seeds", DecoBlocks.BEAMER);
    public static final Item BEAM_FRUIT = registerAliasedBlockItem("beam_fruit", DecoBlocks.BEAMER);
    public static final Item THUJA_SEEDS = registerAliasedBlockItem("thuja_seeds", DecoBlocks.THUJA);

    // metals
    public static final Item AZEL_INGOT = registerSimpleItem("azel_ingot");
    public static final Item AZEL_NUGGET = registerSimpleItem("azel_nugget");
    public static final Item ETHRIL_INGOT = registerSimpleItem("ethril_ingot");
    public static final Item ETHRIL_NUGGET = registerSimpleItem("ethril_nugget");
    public static final Item EBONY_INGOT = registerSimpleItem("ebony_ingot");
    public static final Item EBONY_NUGGET = registerSimpleItem("ebony_nugget");

    // attrahite
    public static final Item ENRICHED_ATTRAHITE = registerSimpleItem("enriched_attrahite");
    public static final Item RAW_AZEL = registerSimpleItem("raw_azel");
    public static final Item ATTRAHITE_BRICK = registerSimpleItem("attrahite_brick");

    public static final Item BINDER = registerSimpleItem("binder");
    public static final Item EBONY = registerSimpleItem("ebony");
    public static final Item RESONATING_WAND = registerSimpleItem("resonating_wand");

    private static Item registerSimpleItem(String id) {
        return Registry.register(Registries.ITEM, EIdentifier.of(id), new Item(new Settings()));
    }

    private static Item registerBlockItem(BlockItem blockItem) {
        blockItem.appendBlocks(Item.BLOCK_ITEMS, blockItem);
        return Registry.register(Registries.ITEM, Registries.BLOCK.getId(blockItem.getBlock()), blockItem);
    }

    public static Item registerAliasedBlockItem(String id, Block block) {
        AliasedBlockItem aliasedBlockItem = new AliasedBlockItem(block, new Settings());
        Registry.register(Registries.ITEM, EIdentifier.of(id), aliasedBlockItem);
        return aliasedBlockItem;
    }

    public static void registerAll() {}
}
