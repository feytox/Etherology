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
    public static final BlockFamily ATTRAHITE_BRICKS;

    public static final BlockFamily SLITHERITE;
    public static final BlockFamily POLISHED_SLITHERITE;
    public static final BlockFamily POLISHED_SLITHERITE_BRICKS;
    public static final BlockFamily CHISELED_POLISHED_SLITHERITE;
    public static final BlockFamily CRACKED_POLISHED_SLITHERITE_BRICKS;
    public static final BlockFamily CHISELED_POLISHED_SLITHERITE_BRICKS;

    public static final BlockFamily[] STONE_FAMILIES;

    public static void registerFamilies() {}

    public static List<Block> getBlocks(BlockFamily blockFamily) {
        List<Block> blocks = new ArrayList<>(blockFamily.getVariants().values().stream().toList());
        blocks.add(blockFamily.getBaseBlock());
        return blocks;
    }

    public static BlockFamily.Builder register(Block baseBlock, BlockFamily.Variant... excludedVariants) {
        return register(baseBlock, false, excludedVariants);
    }

    public static BlockFamily.Builder register(Block baseBlock, boolean skipModelGeneration, BlockFamily.Variant... excludedVariants) {
        BlockFamily.Builder builder = BlockFamilies.register(baseBlock);
        BlockFamily family = builder.build();
        if (family instanceof BlockFamilyAccess access) {
            access.etherology$skipModelGeneration(skipModelGeneration);
            if (excludedVariants.length > 0)
                access.etherology$addExcludedVariants(excludedVariants);
        }
        FAMILIES.add(family);
        return builder;
    }

    static {
        PEACH = register(ExtraBlocksRegistry.PEACH_PLANKS).button(PEACH_BUTTON).door(PEACH_DOOR).fence(PEACH_FENCE).fenceGate(PEACH_FENCE_GATE).pressurePlate(PEACH_PRESSURE_PLATE).sign(PEACH_SIGN, PEACH_WALL_SIGN).slab(PEACH_SLAB).stairs(PEACH_STAIRS).trapdoor(PEACH_TRAPDOOR).group("wooden").unlockCriterionName("has_planks").build();

        SLITHERITE = register(DecoBlocks.SLITHERITE, BlockFamily.Variant.POLISHED)
                .stairs(SLITHERITE_STAIRS)
                .slab(SLITHERITE_SLAB)
                .wall(SLITHERITE_WALL)
                .polished(DecoBlocks.POLISHED_SLITHERITE).build();
        POLISHED_SLITHERITE = register(DecoBlocks.POLISHED_SLITHERITE, BlockFamily.Variant.CHISELED)
                .slab(POLISHED_SLITHERITE_SLAB)
                .stairs(POLISHED_SLITHERITE_STAIRS)
                .wall(POLISHED_SLITHERITE_WALL)
                .button(POLISHED_SLITHERITE_BUTTON)
                .pressurePlate(POLISHED_SLITHERITE_PRESSURE_PLATE)
                .chiseled(DecoBlocks.CHISELED_POLISHED_SLITHERITE).build();
        POLISHED_SLITHERITE_BRICKS = register(DecoBlocks.POLISHED_SLITHERITE_BRICKS, BlockFamily.Variant.CHISELED, BlockFamily.Variant.CRACKED)
                .slab(POLISHED_SLITHERITE_BRICK_SLAB)
                .stairs(POLISHED_SLITHERITE_BRICK_STAIRS)
                .wall(POLISHED_SLITHERITE_BRICK_WALL)
                .chiseled(DecoBlocks.CHISELED_POLISHED_SLITHERITE_BRICKS)
                .cracked(DecoBlocks.CRACKED_POLISHED_SLITHERITE_BRICKS).build();

        CHISELED_POLISHED_SLITHERITE = register(DecoBlocks.CHISELED_POLISHED_SLITHERITE).build();
        CHISELED_POLISHED_SLITHERITE_BRICKS = register(DecoBlocks.CHISELED_POLISHED_SLITHERITE_BRICKS, true).build();
        CRACKED_POLISHED_SLITHERITE_BRICKS = register(DecoBlocks.CRACKED_POLISHED_SLITHERITE_BRICKS).build();

        ATTRAHITE_BRICKS = register(DecoBlocks.ATTRAHITE_BRICKS).slab(ATTRAHITE_BRICK_SLAB).stairs(ATTRAHITE_BRICK_STAIRS).build();

        STONE_FAMILIES = new BlockFamily[]{ATTRAHITE_BRICKS, SLITHERITE, POLISHED_SLITHERITE, POLISHED_SLITHERITE_BRICKS, CHISELED_POLISHED_SLITHERITE, CHISELED_POLISHED_SLITHERITE_BRICKS, CRACKED_POLISHED_SLITHERITE_BRICKS};
    }
}
