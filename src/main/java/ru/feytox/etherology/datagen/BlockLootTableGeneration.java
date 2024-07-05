package ru.feytox.etherology.datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.etherealChannel.EtherealChannel;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.util.misc.RandomChanceWithFortuneCondition;

import java.util.Map;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL_CASE;
import static ru.feytox.etherology.registry.item.DecoBlockItems.ENRICHED_ATTRAHITE;
import static ru.feytox.etherology.registry.item.DecoBlockItems.THUJA_SEEDS;

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
        addDrop(WEEPING_PEACH_LOG, drops(WEEPING_PEACH_LOG, PEACH_LOG));

        addDrop(ATTRAHITE, dropsWithSilkTouch(ATTRAHITE, applyExplosionDecay(ATTRAHITE, ItemEntry.builder(ENRICHED_ATTRAHITE).conditionally(RandomChanceWithFortuneCondition.builder(0.02F, 0.02F)))));
        generateChannelDrop(ETHEREAL_CHANNEL, ETHEREAL_CHANNEL_CASE);

        addDrop(THUJA, THUJA_SEEDS);
        addDrop(THUJA_PLANT, THUJA_SEEDS);
        addPottedPlantDrops(POTTED_BEAMER);
        addPottedPlantDrops(POTTED_THUJA);
        addPottedPlantDrops(POTTED_PEACH_SAPLING);

        addDrop(LIGHTELET, this::grassDrops);

        BLOCKS_TO_DROP = null;
    }

    private void generateChannelDrop(Block channel, Block channelCase) {
        addDrop(channel, LootTable.builder()
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(this.applyExplosionDecay(channel, ItemEntry.builder(channel))))
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(this.applyExplosionDecay(channelCase, ItemEntry.builder(channelCase)))
                        .conditionally(BlockStatePropertyLootCondition.builder(channel)
                                .properties(StatePredicate.Builder.create().exactMatch(EtherealChannel.IN_CASE, true)))));
    }

    public static void generateDrop(Block block, @Nullable ItemConvertible drop) {
        if (!FabricDataGenHelper.ENABLED) return;
        if (BLOCKS_TO_DROP == null) BLOCKS_TO_DROP = new Object2ObjectOpenHashMap<>();
        BLOCKS_TO_DROP.put(block, drop);
    }
}
