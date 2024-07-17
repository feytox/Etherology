package ru.feytox.etherology.datagen;

import lombok.val;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.data.EBlockTags;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.registry.block.EBlockFamilies.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;

public class BlockTagGeneration extends FabricTagProvider.BlockTagProvider {

    public BlockTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        addBlocks(BlockTags.STONE_BRICKS, DecoBlocks.SLITHERITE_BRICKS, DecoBlocks.CHISELED_SLITHERITE_BRICKS, DecoBlocks.CRACKED_SLITHERITE_BRICKS);

        // add all, that defined in variants
        BlockFamily[] stoneFamilies = {SLITHERITE, SLITHERITE_BRICKS, CHISELED_SLITHERITE_BRICKS, CRACKED_SLITHERITE_BRICKS, POLISHED_SLITHERITE, ATTRAHITE_BRICKS};
        addAllBlocks(BlockTags.PICKAXE_MINEABLE, stoneFamilies);
        addVariant(BlockTags.SLABS, BlockFamily.Variant.SLAB, stoneFamilies);
        addVariant(BlockTags.STAIRS, BlockFamily.Variant.STAIRS, stoneFamilies);
        addVariant(BlockTags.WALLS, BlockFamily.Variant.WALL, stoneFamilies);
        addVariant(BlockTags.STONE_PRESSURE_PLATES, BlockFamily.Variant.PRESSURE_PLATE, stoneFamilies);

        addBlocks(EBlockTags.PEACH_LOGS, DecoBlocks.PEACH_LOG, DecoBlocks.PEACH_WOOD, DecoBlocks.STRIPPED_PEACH_LOG, DecoBlocks.STRIPPED_PEACH_WOOD);
        addBlocks(BlockTags.WOODEN_BUTTONS, DecoBlocks.PEACH_BUTTON);
        addBlocks(BlockTags.WOODEN_DOORS, DecoBlocks.PEACH_DOOR);
        addBlocks(BlockTags.PLANKS, ExtraBlocksRegistry.PEACH_PLANKS);
        addBlocks(BlockTags.WOODEN_STAIRS, DecoBlocks.PEACH_STAIRS);
        addBlocks(BlockTags.WOODEN_SLABS, DecoBlocks.PEACH_SLAB);
        addBlocks(BlockTags.WOODEN_FENCES, DecoBlocks.PEACH_FENCE);
        addBlocks(BlockTags.FENCE_GATES, DecoBlocks.PEACH_FENCE_GATE);
        addBlocks(BlockTags.WOODEN_PRESSURE_PLATES, DecoBlocks.PEACH_PRESSURE_PLATE);
        addBlocks(BlockTags.WOODEN_TRAPDOORS, DecoBlocks.PEACH_TRAPDOOR);
        addBlocks(BlockTags.SIGNS, DecoBlocks.PEACH_SIGN);
        addBlocks(BlockTags.WALL_SIGNS, DecoBlocks.PEACH_WALL_SIGN);
        addBlocks(BlockTags.CEILING_HANGING_SIGNS, DecoBlocks.PEACH_HANGING_SIGN);
        addBlocks(BlockTags.WALL_HANGING_SIGNS, DecoBlocks.PEACH_WALL_HANGING_SIGN);
        addBlocks(BlockTags.LEAVES, DecoBlocks.PEACH_LEAVES);
        addTags(BlockTags.LOGS, EBlockTags.PEACH_LOGS);
        addTags(BlockTags.LOGS_THAT_BURN, EBlockTags.PEACH_LOGS);
        addBlocks(BlockTags.LOGS, DecoBlocks.WEEPING_PEACH_LOG);
        addBlocks(BlockTags.LOGS_THAT_BURN, DecoBlocks.WEEPING_PEACH_LOG);

        addBlocks(BlockTags.FLOWERS, DecoBlocks.BEAMER);
        addBlocks(BlockTags.SAPLINGS, DecoBlocks.PEACH_SAPLING);
        addBlocks(BlockTags.REPLACEABLE_BY_TREES, DecoBlocks.THUJA, DecoBlocks.THUJA_PLANT, DecoBlocks.LIGHTELET);

        addBlocks(BlockTags.BEACON_BASE_BLOCKS, DecoBlocks.EBONY_BLOCK, DecoBlocks.ETHRIL_BLOCK);
        addBlocks(BlockTags.SMALL_FLOWERS, DecoBlocks.BEAMER);
        addBlocks(BlockTags.FLOWER_POTS, DecoBlocks.POTTED_BEAMER);
        addBlocks(BlockTags.FLOWER_POTS, DecoBlocks.POTTED_THUJA);
        addBlocks(BlockTags.FLOWER_POTS, DecoBlocks.POTTED_PEACH_SAPLING);

        Block[] needsStonePick = {BREWING_CAULDRON, ETHEREAL_STORAGE, ETHEREAL_CHANNEL, ETHEREAL_FORK, ETHEREAL_SOCKET, ETHEREAL_FURNACE, ETHEREAL_SPINNER, SAMOVAR_BLOCK, DecoBlocks.ATTRAHITE, TUNING_FORK};
        Block[] needsIronPick = {DecoBlocks.AZEL_BLOCK, DecoBlocks.EBONY_BLOCK, DecoBlocks.ETHRIL_BLOCK, ETHEREAL_METRONOME};
        addBlocks(BlockTags.PICKAXE_MINEABLE, PEDESTAL_BLOCK, SEDIMENTARY_BLOCK, JUG, CLAY_JUG, ARMILLARY_MATRIX, JEWELRY_TABLE);
        addBlocks(BlockTags.PICKAXE_MINEABLE, needsStonePick);
        addBlocks(BlockTags.PICKAXE_MINEABLE, needsIronPick);
        addBlocks(BlockTags.NEEDS_STONE_TOOL, needsStonePick);
        addBlocks(BlockTags.NEEDS_IRON_TOOL, needsIronPick);

        addAllBlocks(BlockTags.AXE_MINEABLE, PEACH);
        addBlocks(BlockTags.AXE_MINEABLE, ESSENCE_DETECTOR_BLOCK, FURNITURE_SLAB, CLOSET_SLAB, SHELF_SLAB, EMPOWERMENT_TABLE, SPILL_BARREL, CRATE, LEVITATOR, INVENTOR_TABLE, ETHEREAL_CHANNEL_CASE, DecoBlocks.LIGHTELET);
        addBlocks(BlockTags.HOE_MINEABLE, DecoBlocks.PEACH_LEAVES, DecoBlocks.FOREST_LANTERN);

        // TODO: 18.02.2024 add to convention tags
    }

    private void addBlocks(TagKey<Block> tagKey, Block... blocks) {
        getOrCreateTagBuilder(tagKey).add(blocks);
    }

    @SafeVarargs
    private void addTags(TagKey<Block> tagKey, TagKey<Block>... tagKeys) {
        val builder = getOrCreateTagBuilder(tagKey);
        Arrays.stream(tagKeys).forEach(builder::addTag);
    }

    private void addAllBlocks(TagKey<Block> tagKey, BlockFamily... blockFamilies) {
        List<Block> allBlocks = new ArrayList<>();
        for (BlockFamily blockFamily : blockFamilies) {
            allBlocks.addAll(EBlockFamilies.getBlocks(blockFamily));
        }
        Block[] blocksArray = allBlocks.toArray(new Block[0]);
        addBlocks(tagKey, blocksArray);
    }

    private void addVariant(TagKey<Block> tagKey, BlockFamily.Variant variant, BlockFamily... blockFamilies) {
        List<Block> blocks = new ArrayList<>();
        for (BlockFamily blockFamily : blockFamilies) {
            if (blockFamily.getVariants().containsKey(variant)) {
                blocks.add(blockFamily.getVariant(variant));
            }
        }
        Block[] blocksArray = blocks.toArray(new Block[0]);
        addBlocks(tagKey, blocksArray);
    }
}
