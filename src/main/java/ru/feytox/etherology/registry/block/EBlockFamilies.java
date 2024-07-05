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

    public static final BlockFamily SLITHERITE;
    public static final BlockFamily CRACKED_SLITHERITE_BRICKS;
    public static final BlockFamily CHISELED_SLITHERITE_BRICKS;
    public static final BlockFamily SLITHERITE_BRICKS;
    public static final BlockFamily POLISHED_SLITHERITE;

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

        SLITHERITE = register(DecoBlocks.SLITHERITE, BlockFamily.Variant.POLISHED).stairs(SLITHERITE_STAIRS).slab(SLITHERITE_SLAB).button(SLITHERITE_BUTTON).pressurePlate(SLITHERITE_PRESSURE_PLATE).wall(SLITHERITE_WALL).polished(DecoBlocks.POLISHED_SLITHERITE).build();
        SLITHERITE_BRICKS = register(DecoBlocks.SLITHERITE_BRICKS, BlockFamily.Variant.CHISELED, BlockFamily.Variant.CRACKED).slab(SLITHERITE_BRICK_SLAB).stairs(SLITHERITE_BRICK_STAIRS).wall(SLITHERITE_BRICK_WALL).chiseled(DecoBlocks.CHISELED_SLITHERITE_BRICKS).cracked(DecoBlocks.CRACKED_SLITHERITE_BRICKS).build();
        CHISELED_SLITHERITE_BRICKS = register(DecoBlocks.CHISELED_SLITHERITE_BRICKS).build();
        CRACKED_SLITHERITE_BRICKS = register(DecoBlocks.CRACKED_SLITHERITE_BRICKS).slab(CRACKED_SLITHERITE_BRICK_SLAB).stairs(CRACKED_SLITHERITE_BRICK_STAIRS).build();
        POLISHED_SLITHERITE = register(DecoBlocks.POLISHED_SLITHERITE).slab(POLISHED_SLITHERITE_SLAB).stairs(POLISHED_SLITHERITE_STAIRS).wall(POLISHED_SLITHERITE_WALL).build();

        ATTRAHITE_BRICKS = register(DecoBlocks.ATTRAHITE_BRICKS).slab(ATTRAHITE_BRICK_SLAB).stairs(ATTRAHITE_BRICK_STAIRS).build();
    }
}
