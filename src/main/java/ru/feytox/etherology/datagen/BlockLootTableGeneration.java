package ru.feytox.etherology.datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.entry.ItemEntry;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.util.feyapi.RandomChanceWithFortuneCondition;

import java.util.Map;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.ENRICHED_ATTRAHITE;

public class BlockLootTableGeneration extends FabricBlockLootTableProvider {

    private static Map<Block, ItemConvertible> BLOCKS_TO_DROP = null;

    protected BlockLootTableGeneration(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        BLOCKS_TO_DROP.forEach((block, drop) -> {
            if (drop == null) addDrop(block);
            else addDrop(block, drop);
        });

        addDrop(PEACH_DOOR, doorDrops(PEACH_DOOR));
        addDrop(PEACH_SIGN, DecoBlockItems.PEACH_SIGN);
        addDrop(PEACH_WALL_SIGN, DecoBlockItems.PEACH_SIGN);
        addDrop(PEACH_LEAVES, leavesDrops(PEACH_LEAVES, PEACH_SAPLING, SAPLING_DROP_CHANCE));
        addDrop(ETHEREAL_STONE, drops(ETHEREAL_STONE, COBBLED_ETHEREAL_STONE));
        addDrop(ATTRAHITE, dropsWithSilkTouch(ATTRAHITE, applyExplosionDecay(ATTRAHITE, ItemEntry.builder(ENRICHED_ATTRAHITE).conditionally(RandomChanceWithFortuneCondition.builder(0.02F, 0.02F)))));

        addPottedPlantDrops(POTTED_BEAMER);

        BLOCKS_TO_DROP = null;
    }

    public static void generateDrop(Block block, @Nullable ItemConvertible drop) {
        if (!FabricDataGenHelper.ENABLED) return;
        if (BLOCKS_TO_DROP == null) BLOCKS_TO_DROP = new Object2ObjectOpenHashMap<>();
        BLOCKS_TO_DROP.put(block, drop);
    }
}
