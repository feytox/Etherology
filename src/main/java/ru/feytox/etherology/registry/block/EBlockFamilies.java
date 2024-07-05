package ru.feytox.etherology.registry.block;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;

@UtilityClass
public class EBlockFamilies {

    public static final List<BlockFamily> FAMILIES = new ObjectArrayList<>();
    
    public static final BlockFamily PEACH;

    public static final BlockFamily ETHEREAL_STONE;
    public static final BlockFamily COBBLED_ETHEREAL_STONE;
    public static final BlockFamily CRACKED_ETHEREAL_STONE_BRICKS;
    public static final BlockFamily CHISELED_ETHEREAL_STONE_BRICKS;
    public static final BlockFamily ETHEREAL_STONE_BRICKS;
    public static final BlockFamily MOSSY_COBBLED_ETHEREAL_STONE;
    public static final BlockFamily MOSSY_ETHEREAL_STONE_BRICKS;
    public static final BlockFamily POLISHED_ETHEREAL_STONE;

    public static final BlockFamily ATTRAHITE_BRICKS;


    public static void registerFamilies() {}


    public static List<Block> getBlocks(BlockFamily blockFamily) {
        List<Block> blocks = new ArrayList<>(blockFamily.getVariants().values().stream().toList());
        blocks.add(blockFamily.getBaseBlock());
        return blocks;
    }

    public static BlockFamily.Builder register(Block baseBlock, BlockFamily.Variant... excludedVariants) {
        BlockFamily.Builder builder = BlockFamilies.register(baseBlock);
        BlockFamily family = builder.build();
        if (excludedVariants.length > 0 && family instanceof BlockFamilyAccess access) {
            access.etherology$addExcludedVariants(excludedVariants);
        }
        FAMILIES.add(family);
        return builder;
    }

    static {
        PEACH = register(PEACH_PLANKS).button(PEACH_BUTTON).door(PEACH_DOOR).fence(PEACH_FENCE).fenceGate(PEACH_FENCE_GATE).pressurePlate(PEACH_PRESSURE_PLATE).sign(PEACH_SIGN, PEACH_WALL_SIGN).slab(PEACH_SLAB).stairs(PEACH_STAIRS).trapdoor(PEACH_TRAPDOOR).group("wooden").unlockCriterionName("has_planks").build();

        ETHEREAL_STONE = register(DecoBlocks.ETHEREAL_STONE, BlockFamily.Variant.POLISHED).stairs(ETHEREAL_STONE_STAIRS).slab(ETHEREAL_STONE_SLAB).button(ETHEREAL_STONE_BUTTON).pressurePlate(ETHEREAL_STONE_PRESSURE_PLATE).wall(ETHEREAL_STONE_WALL).polished(DecoBlocks.POLISHED_ETHEREAL_STONE).build();
        COBBLED_ETHEREAL_STONE = register(DecoBlocks.COBBLED_ETHEREAL_STONE).slab(COBBLED_ETHEREAL_STONE_SLAB).stairs(COBBLED_ETHEREAL_STONE_STAIRS).wall(COBBLED_ETHEREAL_STONE_WALL).build();
        ETHEREAL_STONE_BRICKS = register(DecoBlocks.ETHEREAL_STONE_BRICKS, BlockFamily.Variant.CHISELED, BlockFamily.Variant.CRACKED).slab(ETHEREAL_STONE_BRICK_SLAB).stairs(ETHEREAL_STONE_BRICK_STAIRS).wall(ETHEREAL_STONE_BRICK_WALL).chiseled(DecoBlocks.CHISELED_ETHEREAL_STONE_BRICKS).cracked(DecoBlocks.CRACKED_ETHEREAL_STONE_BRICKS).build();
        CHISELED_ETHEREAL_STONE_BRICKS = register(DecoBlocks.CHISELED_ETHEREAL_STONE_BRICKS).stairs(CHISELED_ETHEREAL_STONE_BRICK_STAIRS).build();
        CRACKED_ETHEREAL_STONE_BRICKS = register(DecoBlocks.CRACKED_ETHEREAL_STONE_BRICKS).slab(CRACKED_ETHEREAL_STONE_BRICK_SLAB).stairs(CRACKED_ETHEREAL_STONE_BRICK_STAIRS).build();
        MOSSY_COBBLED_ETHEREAL_STONE = register(DecoBlocks.MOSSY_COBBLED_ETHEREAL_STONE).slab(MOSSY_COBBLED_ETHEREAL_STONE_SLAB).stairs(MOSSY_COBBLED_ETHEREAL_STONE_STAIRS).build();
        MOSSY_ETHEREAL_STONE_BRICKS = register(DecoBlocks.MOSSY_ETHEREAL_STONE_BRICKS).slab(MOSSY_ETHEREAL_STONE_BRICK_SLAB).stairs(MOSSY_ETHEREAL_STONE_BRICK_STAIRS).build();
        POLISHED_ETHEREAL_STONE = register(DecoBlocks.POLISHED_ETHEREAL_STONE).slab(POLISHED_ETHEREAL_STONE_SLAB).stairs(POLISHED_ETHEREAL_STONE_STAIRS).build();

        ATTRAHITE_BRICKS = register(DecoBlocks.ATTRAHITE_BRICKS).slab(ATTRAHITE_BRICK_SLAB).stairs(ATTRAHITE_BRICK_STAIRS).build();
    }
}
