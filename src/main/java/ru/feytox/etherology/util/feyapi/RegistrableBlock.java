package ru.feytox.etherology.util.feyapi;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface RegistrableBlock {
    Block getBlockInstance();
    String getBlockId();

    default Item getItem() {
        return Registries.ITEM.get(new EIdentifier(getBlockId()));
    }

    default Block registerAll() {
        Block block = registerBlock();
        registerItem();
        return block;
    }

    default Block registerBlock() {
        Block block = getBlockInstance();
        String blockId = getBlockId();
        return Registry.register(Registries.BLOCK, new EIdentifier(blockId), block);
    }

    default BlockItem registerItem() {
        Block block = getBlockInstance();
        String blockId = getBlockId();
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        return Registry.register(Registries.ITEM, new EIdentifier(blockId), blockItem);
    }
}
