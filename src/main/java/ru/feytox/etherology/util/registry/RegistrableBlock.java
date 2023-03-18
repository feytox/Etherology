package ru.feytox.etherology.util.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public interface RegistrableBlock {
    Block getBlockInstance();
    String getBlockId();

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
        // TODO: 06/03/2023 насчёт настроек подумать
        BlockItem blockItem = new BlockItem(block, new FabricItemSettings());
        return Registry.register(Registries.ITEM, new EIdentifier(blockId), blockItem);
    }
}
