package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.DecoBlocks;
import ru.feytox.etherology.EBlockFamilies;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.EBlockFamilies.*;

public class BlockTagGeneration extends FabricTagProvider.BlockTagProvider {
    public BlockTagGeneration(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        addBlocks(BlockTags.BASE_STONE_OVERWORLD, DecoBlocks.ETHEREAL_STONE);
        addBlocks(BlockTags.STONE_ORE_REPLACEABLES, DecoBlocks.ETHEREAL_STONE);
        addBlocks(BlockTags.STONE_BRICKS, DecoBlocks.ETHEREAL_STONE_BRICKS, DecoBlocks.CHISELED_ETHEREAL_STONE_BRICKS, DecoBlocks.CRACKED_ETHEREAL_STONE_BRICKS, DecoBlocks.MOSSY_ETHEREAL_STONE_BRICKS);

        // добавление всего, что указано в вариантах
        BlockFamily[] mostFamilies = new BlockFamily[]{ETHEREAL_STONE, COBBLED_ETHEREAL_STONE, ETHEREAL_STONE_BRICKS, CHISELED_ETHEREAL_STONE_BRICKS, CRACKED_ETHEREAL_STONE_BRICKS, MOSSY_COBBLED_ETHEREAL_STONE, MOSSY_ETHEREAL_STONE_BRICKS, POLISHED_ETHEREAL_STONE, CLAY_TILE, BLUE_CLAY_TILE, GREEN_CLAY_TILE, RED_CLAY_TILE, YELLOW_CLAY_TILE};
        addAllBlocks(BlockTags.PICKAXE_MINEABLE, mostFamilies);
        addVariant(BlockTags.SLABS, BlockFamily.Variant.SLAB, mostFamilies);
        addVariant(BlockTags.STAIRS, BlockFamily.Variant.STAIRS, mostFamilies);
        addVariant(BlockTags.WALLS, BlockFamily.Variant.WALL, mostFamilies);
        addVariant(BlockTags.STONE_PRESSURE_PLATES, BlockFamily.Variant.PRESSURE_PLATE, mostFamilies);

        addBlocks(BlockTags.FLOWERS, DecoBlocks.BEAMER);

        addBlocks(BlockTags.NEEDS_IRON_TOOL, DecoBlocks.ATTRAHITE_BLOCK, DecoBlocks.TELDER_STEEL_BLOCK, DecoBlocks.ETHRIL_BLOCK);
    }

    private void addBlocks(TagKey<Block> tagKey, Block... blocks) {
        getOrCreateTagBuilder(tagKey).add(blocks);
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
