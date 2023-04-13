package ru.feytox.etherology;

import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamilies;
import net.minecraft.data.family.BlockFamily;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.DecoBlocks.*;

public class EBlockFamilies {
    public static final BlockFamily PEACH;

    public static final BlockFamily ETHEREAL_STONE;
    public static final BlockFamily COBBLED_ETHEREAL_STONE;
    public static final BlockFamily CRACKED_ETHEREAL_STONE_BRICKS;
    public static final BlockFamily CHISELED_ETHEREAL_STONE_BRICKS;
    public static final BlockFamily ETHEREAL_STONE_BRICKS;
    public static final BlockFamily MOSSY_COBBLED_ETHEREAL_STONE;
    public static final BlockFamily MOSSY_ETHEREAL_STONE_BRICKS;
    public static final BlockFamily POLISHED_ETHEREAL_STONE;

    public static final BlockFamily CLAY_TILE;
    public static final BlockFamily BLUE_CLAY_TILE;
    public static final BlockFamily GREEN_CLAY_TILE;
    public static final BlockFamily RED_CLAY_TILE;
    public static final BlockFamily YELLOW_CLAY_TILE;


    public static void registerFamilies() {}



    public static List<Block> getBlocks(BlockFamily blockFamily) {
        List<Block> blocks = new ArrayList<>(blockFamily.getVariants().values().stream().toList());
        blocks.add(blockFamily.getBaseBlock());
        return blocks;
    }

    // TODO: 12/04/2023 для камней нет привязки polished/cracked и т.д.

    static {
        PEACH = BlockFamilies.register(PEACH_PLANKS).button(PEACH_BUTTON).door(PEACH_DOOR).fence(PEACH_FENCE).fenceGate(PEACH_FENCE_GATE).pressurePlate(PEACH_PRESSURE_PLATE).sign(PEACH_SIGN, PEACH_WALL_SIGN).slab(PEACH_SLAB).stairs(PEACH_STAIRS).trapdoor(PEACH_TRAPDOOR).group("wooden").unlockCriterionName("has_planks").build();

        ETHEREAL_STONE = BlockFamilies.register(DecoBlocks.ETHEREAL_STONE).stairs(ETHEREAL_STONE_STAIRS).slab(ETHEREAL_STONE_SLAB).button(ETHEREAL_STONE_BUTTON).pressurePlate(ETHEREAL_STONE_PRESSURE_PLATE).wall(ETHEREAL_STONE_WALL).build();
        COBBLED_ETHEREAL_STONE = BlockFamilies.register(DecoBlocks.COBBLED_ETHEREAL_STONE).slab(COBBLED_ETHEREAL_STONE_SLAB).stairs(COBBLED_ETHEREAL_STONE_STAIRS).wall(COBBLED_ETHEREAL_STONE_WALL).build();
        ETHEREAL_STONE_BRICKS = BlockFamilies.register(DecoBlocks.ETHEREAL_STONE_BRICKS).slab(ETHEREAL_STONE_BRICK_SLAB).stairs(ETHEREAL_STONE_BRICK_STAIRS).wall(ETHEREAL_STONE_BRICK_WALL).build();
        CHISELED_ETHEREAL_STONE_BRICKS = BlockFamilies.register(DecoBlocks.CHISELED_ETHEREAL_STONE_BRICKS).stairs(CHISELED_ETHEREAL_STONE_BRICK_STAIRS).build();
        CRACKED_ETHEREAL_STONE_BRICKS = BlockFamilies.register(DecoBlocks.CRACKED_ETHEREAL_STONE_BRICKS).slab(CRACKED_ETHEREAL_STONE_BRICK_SLAB).stairs(CRACKED_ETHEREAL_STONE_BRICK_STAIRS).build();
        MOSSY_COBBLED_ETHEREAL_STONE = BlockFamilies.register(DecoBlocks.MOSSY_COBBLED_ETHEREAL_STONE).slab(MOSSY_COBBLED_ETHEREAL_STONE_SLAB).stairs(MOSSY_COBBLED_ETHEREAL_STONE_STAIRS).build();
        MOSSY_ETHEREAL_STONE_BRICKS = BlockFamilies.register(DecoBlocks.MOSSY_ETHEREAL_STONE_BRICKS).slab(MOSSY_ETHEREAL_STONE_BRICK_SLAB).stairs(MOSSY_ETHEREAL_STONE_BRICK_STAIRS).build();
        POLISHED_ETHEREAL_STONE = BlockFamilies.register(DecoBlocks.POLISHED_ETHEREAL_STONE).slab(POLISHED_ETHEREAL_STONE_SLAB).stairs(POLISHED_ETHEREAL_STONE_STAIRS).build();

        CLAY_TILE = BlockFamilies.register(CLAY_TILES).slab(CLAY_TILES_SLAB).stairs(CLAY_TILES_STAIRS).wall(CLAY_TILES_WALL).build();
        BLUE_CLAY_TILE = BlockFamilies.register(BLUE_CLAY_TILES).slab(BLUE_CLAY_TILES_SLAB).stairs(BLUE_CLAY_TILES_STAIRS).wall(BLUE_CLAY_TILES_WALL).build();
        GREEN_CLAY_TILE = BlockFamilies.register(GREEN_CLAY_TILES).slab(GREEN_CLAY_TILES_SLAB).stairs(GREEN_CLAY_TILES_STAIRS).wall(GREEN_CLAY_TILES_WALL).build();
        RED_CLAY_TILE = BlockFamilies.register(RED_CLAY_TILES).slab(RED_CLAY_TILES_SLAB).stairs(RED_CLAY_TILES_STAIRS).wall(RED_CLAY_TILES_WALL).build();
        YELLOW_CLAY_TILE = BlockFamilies.register(YELLOW_CLAY_TILES).slab(YELLOW_CLAY_TILES_SLAB).stairs(YELLOW_CLAY_TILES_STAIRS).wall(YELLOW_CLAY_TILES_WALL).build();
    }
}
