package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.item.DecoBlockItems;

import java.util.concurrent.CompletableFuture;

public class ItemTagGeneration extends FabricTagProvider.ItemTagProvider {
    public ItemTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        addItems(ItemTags.BEACON_PAYMENT_ITEMS, DecoBlockItems.TELDER_STEEL_INGOT, DecoBlockItems.ETHRIL_INGOT);
    }

    public void copy(TagKey<Block>[] blockTags, TagKey<Item>[] itemTags) throws Exception {
        if (blockTags.length != itemTags.length) throw new Exception("Block and item tags must have the same length");
        for (int i = 0; i < blockTags.length; i++) {
            copy(blockTags[i], itemTags[i]);
        }
    }

    private void addItems(TagKey<Item> tagKey, Item... items) {
        getOrCreateTagBuilder(tagKey).add(items);
    }
}
