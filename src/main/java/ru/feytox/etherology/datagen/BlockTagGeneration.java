package ru.feytox.etherology.datagen;

import lombok.val;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.registry.block.EBlockFamilies.*;

public class BlockTagGeneration extends FabricTagProvider.BlockTagProvider {

    public static final TagKey<Block> PEACH_LOGS = TagKey.of(RegistryKeys.BLOCK, new EIdentifier("peach_logs"));

    public BlockTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        addBlocks(BlockTags.BASE_STONE_OVERWORLD, DecoBlocks.ETHEREAL_STONE);
        addBlocks(BlockTags.STONE_ORE_REPLACEABLES, DecoBlocks.ETHEREAL_STONE);
        addBlocks(BlockTags.STONE_BRICKS, DecoBlocks.ETHEREAL_STONE_BRICKS, DecoBlocks.CHISELED_ETHEREAL_STONE_BRICKS, DecoBlocks.CRACKED_ETHEREAL_STONE_BRICKS, DecoBlocks.MOSSY_ETHEREAL_STONE_BRICKS);

        // добавление всего, что указано в вариантах
        BlockFamily[] stoneFamilies = {ETHEREAL_STONE, COBBLED_ETHEREAL_STONE, ETHEREAL_STONE_BRICKS, CHISELED_ETHEREAL_STONE_BRICKS, CRACKED_ETHEREAL_STONE_BRICKS, MOSSY_COBBLED_ETHEREAL_STONE, MOSSY_ETHEREAL_STONE_BRICKS, POLISHED_ETHEREAL_STONE, CLAY_TILE, BLUE_CLAY_TILE, GREEN_CLAY_TILE, RED_CLAY_TILE, YELLOW_CLAY_TILE};
        addAllBlocks(BlockTags.PICKAXE_MINEABLE, stoneFamilies);
        addVariant(BlockTags.SLABS, BlockFamily.Variant.SLAB, stoneFamilies);
        addVariant(BlockTags.STAIRS, BlockFamily.Variant.STAIRS, stoneFamilies);
        addVariant(BlockTags.WALLS, BlockFamily.Variant.WALL, stoneFamilies);
        addVariant(BlockTags.STONE_PRESSURE_PLATES, BlockFamily.Variant.PRESSURE_PLATE, stoneFamilies);

        addBlocks(PEACH_LOGS, DecoBlocks.PEACH_LOG, DecoBlocks.PEACH_WOOD, DecoBlocks.STRIPPED_PEACH_LOG, DecoBlocks.STRIPPED_PEACH_WOOD);
        addBlocks(BlockTags.WOODEN_BUTTONS, DecoBlocks.PEACH_BUTTON);
        addBlocks(BlockTags.WOODEN_DOORS, DecoBlocks.PEACH_DOOR);
        addBlocks(BlockTags.PLANKS, DecoBlocks.PEACH_PLANKS);
        addBlocks(BlockTags.WOODEN_STAIRS, DecoBlocks.PEACH_STAIRS);
        addBlocks(BlockTags.WOODEN_SLABS, DecoBlocks.PEACH_SLAB);
        addBlocks(BlockTags.WOODEN_FENCES, DecoBlocks.PEACH_FENCE);
        addBlocks(BlockTags.FENCE_GATES, DecoBlocks.PEACH_FENCE_GATE);
        addBlocks(BlockTags.WOODEN_PRESSURE_PLATES, DecoBlocks.PEACH_PRESSURE_PLATE);
        addBlocks(BlockTags.WOODEN_TRAPDOORS, DecoBlocks.PEACH_TRAPDOOR);
        addBlocks(BlockTags.SIGNS, DecoBlocks.PEACH_SIGN);
        addBlocks(BlockTags.WALL_SIGNS, DecoBlocks.PEACH_WALL_SIGN);
        addBlocks(BlockTags.LEAVES, DecoBlocks.PEACH_LEAVES);
        addTags(BlockTags.LOGS, PEACH_LOGS);
        addTags(BlockTags.LOGS_THAT_BURN, PEACH_LOGS);

        addBlocks(BlockTags.FLOWERS, DecoBlocks.BEAMER);
        addBlocks(BlockTags.SAPLINGS, DecoBlocks.PEACH_SAPLING);
        addBlocks(BlockTags.NEEDS_IRON_TOOL, DecoBlocks.ATTRAHITE_BLOCK, DecoBlocks.TELDER_STEEL_BLOCK, DecoBlocks.ETHRIL_BLOCK);

        addBlocks(BlockTags.BEACON_BASE_BLOCKS, DecoBlocks.TELDER_STEEL_BLOCK, DecoBlocks.ETHRIL_BLOCK);
        addBlocks(BlockTags.SMALL_FLOWERS, DecoBlocks.BEAMER);
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
