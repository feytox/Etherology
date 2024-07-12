package ru.feytox.etherology.registry.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

@UtilityClass
public class AutoBlockLootTable {

    private static Map<Block, ItemConvertible> BLOCKS_TO_DROP = null;

    public static void markAsAuto(Block block, @Nullable ItemConvertible drop) {
        if (!FabricDataGenHelper.ENABLED) return;
        if (BLOCKS_TO_DROP == null) BLOCKS_TO_DROP = new Object2ObjectOpenHashMap<>();
        BLOCKS_TO_DROP.put(block, drop);
    }

    public static void acceptData(BiConsumer<Block, @Nullable ItemConvertible> consumer) {
        BLOCKS_TO_DROP.forEach(consumer);
        BLOCKS_TO_DROP = null;
    }
}
