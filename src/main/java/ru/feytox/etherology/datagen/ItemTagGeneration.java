package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ItemTagGeneration extends FabricTagProvider.ItemTagProvider {

    public static final TagKey<Item> PEACH_LOGS = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("peach_logs"));

    public ItemTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.DOORS, ItemTags.DOORS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        copy(BlockTags.FENCES, ItemTags.FENCES);

        addItems(ItemTags.BEACON_PAYMENT_ITEMS, DecoBlockItems.EBONY_INGOT, DecoBlockItems.ETHRIL_INGOT);
        copy(BlockTagGeneration.PEACH_LOGS, PEACH_LOGS);

        // TODO: 18.02.2024 add to common tags
    }

    public void copy(TagKey<Block>[] blockTags, TagKey<Item>[] itemTags) throws Exception {
        if (blockTags.length != itemTags.length) throw new Exception("Block and item tags must have the same length");
        for (int i = 0; i < blockTags.length; i++) {
            copy(blockTags[i], itemTags[i]);
        }
    }

    private void addItems(TagKey<Item> tagKey, ItemConvertible... items) {
        Arrays.stream(items).map(ItemConvertible::asItem).forEach(getOrCreateTagBuilder(tagKey)::add);
    }
}
