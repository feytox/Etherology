package ru.feytox.etherology.world.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.world.TreesRegistry;

import java.util.function.BiConsumer;

public class PeachFoliagePlacer extends FoliagePlacer {

    public static final Codec<PeachFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
            fillFoliagePlacerFields(instance).apply(instance, PeachFoliagePlacer::new));
    private static final float HIGH_CHANCE = 0.97f;
    private static final float LOW_CHANCE = 0.08f;

    public PeachFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return TreesRegistry.PEACH_FOLIAGE_PLACER;
    }

    // TODO: 18.06.2024 consider to simplify
    // giantTrunk in this case: true = tree top, false = branch
    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        if (treeNode.isGiantTrunk()) generateTop(world, replacer, random, config, treeNode);
        else generateBranch(world, replacer, random, config, treeNode);
    }

    private void generateTop(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, TreeNode treeNode) {
        BlockPos startPos = treeNode.getCenter();
        placeLeaves(world, replacer, random, config, startPos, null, 1, HIGH_CHANCE, 0);
        placeLeaves(world, replacer, random, config, startPos.up(1), null, 1, 1, HIGH_CHANCE);
        placeLeaves(world, replacer, random, config, startPos.up(2), null, 1, 1, LOW_CHANCE);
        placeLeaves(world, replacer, random, config, startPos.up(3), null, 1, HIGH_CHANCE, 0);
        placeLeaves(world, replacer, random, config, startPos.up(4), null, 1, LOW_CHANCE, 0);
        randomPlace(world, replacer, random, config, startPos.up(5), HIGH_CHANCE);
    }

    private void generateBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, TreeNode treeNode) {
        BlockPos startPos = treeNode.getCenter();
        Offset trunkOffset = findTrunk(world, startPos);
        if (trunkOffset == null) return;

        placeLeaves(world, replacer, random, config, startPos.down(), trunkOffset, 1, LOW_CHANCE, 0);
        placeLeaves(world, replacer, random, config, startPos, trunkOffset, 1, 1, HIGH_CHANCE);
        placeLeaves(world, replacer, random, config, startPos.up(), trunkOffset, 1, HIGH_CHANCE, LOW_CHANCE);
    }

    @Nullable
    private Offset findTrunk(TestableWorld world, BlockPos centerPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 && dz != 0) continue;
                if (isLog(world, centerPos.add(dx, 0, dz))) return new Offset(dx, dz);
            }
        }
        return null;
    }

    private boolean isLog(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
    }

    private void placeLeaves(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos center, @Nullable Offset trunkOffset, float centerChance, float mainChance, float cornerChance) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (trunkOffset != null && isTrunk(trunkOffset, dx, dz)) continue;
                float chance = dx != 0 && dz != 0 ? cornerChance : mainChance;
                if (dx == 0 && dz == 0) chance = centerChance;
                randomPlace(world, replacer, random, config, center.add(dx, 0, dz), chance);
            }
        }
    }

    private void randomPlace(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos blockPos, float chance) {
        if (chance == 0 || (chance != 1 && random.nextFloat() > chance)) return;
        placeFoliageBlock(world, replacer, random, config, blockPos);
    }

    private boolean isTrunk(Offset trunkOffset, int dx, int dz) {
        return trunkOffset.dx != 0 && dx == trunkOffset.dx || trunkOffset.dz != 0 && dz == trunkOffset.dz;
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 6;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return false;
    }

    private record Offset(int dx, int dz) {}
}
