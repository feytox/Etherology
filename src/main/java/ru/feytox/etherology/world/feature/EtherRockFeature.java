package ru.feytox.etherology.world.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import ru.feytox.etherology.registry.block.DecoBlocks;

import java.util.Map;

/**
 * @see net.minecraft.world.gen.feature.ForestRockFeature
 */
public class EtherRockFeature extends Feature<DefaultFeatureConfig> {

    private static final int RADIUS = 2;
    private static final float CARPET_CHANCE = 1 / 4f;
    private static final float MOSS_CHANCE = 1 / 6f;

    public EtherRockFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos centerPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();

        while (centerPos.getY() > world.getBottomY() + RADIUS+1) {
            if (!world.isAir(centerPos.down())) {
                BlockState state = world.getBlockState(centerPos.down());
                if (isSoil(state) || isStone(state)) {
                    break;
                }
            }
            centerPos = centerPos.down();
        }

        if (centerPos.getY() <= world.getBottomY() + RADIUS+1) return false;

        Map<Point, Integer> topBlocks = new Object2IntOpenHashMap<>();
        for (int i = 0; i < 3; ++i) {
            int dx = random.nextInt(RADIUS);
            int dy = random.nextInt(RADIUS);
            int dz = random.nextInt(RADIUS);
            float f = (dx + dy + dz) * 0.333F + 0.5F;

            for (BlockPos pos : BlockPos.iterate(centerPos.add(-dx, -dy, -dz), centerPos.add(dx, dy, dz))) {
                if (pos.getSquaredDistance(centerPos) > (double) (f * f)) continue;
                world.setBlockState(pos, DecoBlocks.ETHEREAL_STONE.getDefaultState(), Block.NO_REDRAW);
                topBlocks.merge(new Point(pos.getX(), pos.getZ()), pos.getY(), Integer::max);
            }

            centerPos = centerPos.add(-1 + random.nextInt(RADIUS), -random.nextInt(RADIUS), -1 + random.nextInt(RADIUS));
        }

        topBlocks.forEach((point, y) -> {
            BlockPos pos = new BlockPos(point.x, y, point.z);
            placeBlock(world, random, pos, MOSS_CHANCE, Blocks.MOSS_BLOCK);
            if (world.isAir(pos.up())) placeBlock(world, random, pos.up(), CARPET_CHANCE, Blocks.MOSS_CARPET);
        });

        return true;
    }

    private static void placeBlock(StructureWorldAccess world, Random random, BlockPos pos, float chance, Block block) {
        if (random.nextFloat() <= chance) world.setBlockState(pos, block.getDefaultState(), Block.NO_REDRAW);
    }

    private record Point(int x, int z) {}
}
