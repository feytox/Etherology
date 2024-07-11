package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.registry.misc.TagsRegistry;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.registry.tag.ItemTags.*;
import static ru.feytox.etherology.registry.item.ArmorItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class ItemTagGeneration extends FabricTagProvider.ItemTagProvider {

    public ItemTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        copy(BlockTags.PLANKS, PLANKS);
        copy(BlockTags.WOODEN_BUTTONS, WOODEN_BUTTONS);
        copy(BlockTags.BUTTONS, BUTTONS);
        copy(BlockTags.WOODEN_DOORS, WOODEN_DOORS);
        copy(BlockTags.WOODEN_STAIRS, WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, WOODEN_SLABS);
        copy(BlockTags.WOODEN_FENCES, WOODEN_FENCES);
        copy(BlockTags.FENCE_GATES, FENCE_GATES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, WOODEN_PRESSURE_PLATES);
        copy(BlockTags.DOORS, DOORS);
        copy(BlockTags.SLABS, SLABS);
        copy(BlockTags.STAIRS, STAIRS);
        copy(BlockTags.WOODEN_TRAPDOORS, WOODEN_TRAPDOORS);
        copy(BlockTags.TRAPDOORS, TRAPDOORS);
        copy(BlockTags.FENCES, FENCES);

        addItems(BEACON_PAYMENT_ITEMS, DecoBlockItems.EBONY_INGOT, DecoBlockItems.ETHRIL_INGOT);
        copy(BlockTagGeneration.PEACH_LOGS, TagsRegistry.PEACH_LOGS);

        addItems(TagsRegistry.TUNING_MACES, TUNING_MACE);
        addItems(TagsRegistry.ETHER_SHIELDS, IRON_SHIELD);

        addItems(AXES, ETHRIL_AXE, EBONY_AXE);
        addItems(HOES, ETHRIL_HOE, EBONY_HOE);
        addItems(PICKAXES, ETHRIL_PICKAXE, EBONY_PICKAXE);
        addItems(PICKAXES, BATTLE_PICKAXES);
        addItems(SHOVELS, ETHRIL_SHOVEL, EBONY_SHOVEL);
        addItems(SWORDS, ETHRIL_SWORD, EBONY_SWORD, BROADSWORD, TUNING_MACE);
        addItems(SWORDS, BATTLE_PICKAXES);
        addItems(HEAD_ARMOR, ETHRIL_HELMET, EBONY_HELMET);
        addItems(CHEST_ARMOR, ETHRIL_CHESTPLATE, EBONY_CHESTPLATE);
        addItems(LEG_ARMOR, ETHRIL_LEGGINGS, EBONY_LEGGINGS);
        addItems(FOOT_ARMOR, ETHRIL_BOOTS, EBONY_BOOTS);

        // TODO: 18.02.2024 add to convention tags
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
