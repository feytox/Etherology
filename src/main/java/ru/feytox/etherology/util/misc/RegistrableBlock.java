package ru.feytox.etherology.util.misc;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.datagen.BlockLootTableGeneration;

public interface RegistrableBlock {
    String getBlockId();

    default Item getItem() {
        return Registries.ITEM.get(EIdentifier.of(getBlockId()));
    }

    default Block registerAll() {
        return registerAll(true, null);
    }

    default Block registerAll(boolean generateDrop) {
        return registerAll(generateDrop, null);
    }

    default Block registerAll(boolean generateDrop, @Nullable ItemConvertible drop) {
        Block block = registerBlock();
        registerItem();
        if (generateDrop) BlockLootTableGeneration.generateDrop(block, drop);
        return block;
    }

    default Block registerBlock() {
        String blockId = getBlockId();
        return Registry.register(Registries.BLOCK, EIdentifier.of(blockId), (Block) this);
    }

    default void registerItem() {
        String blockId = getBlockId();
        BlockItem blockItem = new BlockItem((Block) this, new FabricItemSettings());
        Registry.register(Registries.ITEM, EIdentifier.of(blockId), blockItem);
    }
}
