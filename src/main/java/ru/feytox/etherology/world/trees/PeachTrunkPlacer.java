package ru.feytox.etherology.world.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static ru.feytox.etherology.world.gen.EtherTreesGeneration.PEACH_TRUNK_PLACER;

public class PeachTrunkPlacer extends TrunkPlacer {

    public static final Codec<PeachTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            fillTrunkPlacerFields(instance).apply(instance, PeachTrunkPlacer::new));

    public PeachTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return PEACH_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        List<BlockPos> leaves = new ObjectArrayList<>();
        generateRoot(new TreeContext(world, replacer, random, config), height, startPos, leaves);
        return leaves.stream().map(pos -> new FoliagePlacer.TreeNode(pos, 0, false)).collect(Collectors.toCollection(ObjectArrayList::new));

    }

    private void generateRoot(TreeContext context, int height, BlockPos startPos, List<BlockPos> leaves) {
        int rootHeight = MathHelper.ceil(height * (0.5 + 0.25 * context.random.nextFloat()));
        // TODO: 24.08.2023 replace using saplings pos
        Vec3i rootOffset = new Vec3i(context.random.nextBoolean() ? 1 : -1, 0, context.random.nextBoolean() ? 1 : -1);
        List<Vec3i> innerOffsets = getInnerOffsets(rootOffset);
        List<Vec3i> outerOffsets = innerOffsets.stream().map(vec -> getOuterOffsets(rootOffset, vec))
                .flatMap(List::stream).collect(Collectors.toCollection(ObjectArrayList::new));
        Vec3i forkPosOffset = null;
        boolean hasBranch = false;
        List<Vec3i> rootPoses = new ObjectArrayList<>();

        for (int dy = 0; dy < rootHeight; dy++) {
            BlockPos currentPos = startPos.up(dy);
            innerOffsets.forEach(offset -> placeWood(context, currentPos.add(offset)));
            if (forkPosOffset != null) break;

            if (dy == rootHeight - 1) {
                Vec3i firstLeaf = getRandomValue(innerOffsets, context.random);
                if (firstLeaf == null) continue;
                leaves.add(currentPos.up().add(firstLeaf));
                continue;
            }

            switch (dy) {
                case 0 -> {
                    outerOffsets.forEach(offset -> {
                        if (context.random.nextFloat() > 0.25f) return;
                        placeWood(context, currentPos.add(offset));
                        rootPoses.add(offset);
                    });
                    continue;
                }
                case 1 -> {
                    outerOffsets.forEach(offset -> {
                        if (!rootPoses.contains(offset) || context.random.nextFloat() > 0.25f) return;
                        placeWood(context, currentPos.add(offset));
                    });
                    continue;
                }
                case 2 -> {
                    continue;
                }
            }

            if (context.random.nextFloat() <= 0.1f + 0.1f * dy) {
                forkPosOffset = getRandomValue(innerOffsets, context.random);
                if (forkPosOffset == null) continue;
                Vec3i forkDirection = forkPosOffset.multiply(2).subtract(rootOffset);
                List<Vec3i> exceptions = ObjectArrayList.of(forkDirection.multiply(-1));
                generateFork(context, currentPos.add(forkPosOffset), forkDirection, rootHeight * 0.75f, 4, leaves, exceptions);
                continue;
            }

            if (hasBranch || context.random.nextFloat() > 0.02 + 0.02 * dy) continue;
            Vec3i branchOffset = getRandomValue(innerOffsets, context.random, forkPosOffset);
            if (branchOffset == null) continue;

            hasBranch = true;
            List<Vec3i> branchDirections = getOuterOffsets(rootOffset, branchOffset);
            Vec3i branchDirection = getRandomValue(branchDirections, context.random);
            if (branchDirection == null) continue;
            generateBranch(context, currentPos.add(branchOffset), branchDirection.subtract(branchOffset), leaves);
        }
    }

    private void generateFork(TreeContext context, BlockPos startPos, Vec3i forkDirection, float height, int maxSize, List<BlockPos> leaves, List<Vec3i> forkExceptions) {
        if (maxSize < 2) return;
        int forkHeight = MathHelper.ceil(height * (0.8f + 0.2f * context.random.nextFloat()));
        int startSize = maxSize == 2 ? 2 : context.random.nextBetween(1, 2);
        int size = startSize;
        Vec3i nextForkPosOffset = null;
        boolean hasBranch = false;

        List<Vec3i> allInnerOffsets = getInnerOffsets(forkDirection);
        List<Vec3i> innerOffsets = new ObjectArrayList<>();
        for (int i = 0; i < size; i++) {
            innerOffsets.add(getRandomValue(allInnerOffsets, context.random, innerOffsets));
        }

        for (int dy = 0; dy < forkHeight; dy++) {
            BlockPos currentPos = startPos.up(dy);
            innerOffsets.forEach(offset -> placeWood(context, currentPos.add(offset)));
            if (dy == 0) continue;
            if (nextForkPosOffset != null) break;

            if (dy == forkHeight - 1) {
                Vec3i firstLeaf = getRandomValue(innerOffsets, context.random);
                if (firstLeaf == null) continue;
                leaves.add(currentPos.up().add(firstLeaf));
                continue;
            }

            if (size < maxSize && context.random.nextFloat() <= 0.33f + 0.075f * dy) {
                size++;
                Vec3i newOffset = getRandomValue(allInnerOffsets, context.random, innerOffsets);
                if (newOffset != null) innerOffsets.add(newOffset);
                continue;
            }

            if (maxSize > 2 && (size == maxSize || size > startSize) && context.random.nextFloat() <= 0.4f + 0.1f * dy) {
                nextForkPosOffset = getRandomValue(innerOffsets, context.random);
                if (nextForkPosOffset == null) continue;
                Vec3i nextForkDirection = nextForkPosOffset.multiply(2).subtract(forkDirection);
                if (forkExceptions.contains(nextForkDirection)) continue;
                forkExceptions.add(nextForkDirection.multiply(-1));
                generateFork(context, currentPos.add(nextForkPosOffset), nextForkDirection, height * 0.75f, size - context.random.nextBetween(1, 2), leaves, forkExceptions);
            }

            if (hasBranch || context.random.nextFloat() > 0.02 + 0.02 * dy) continue;
            Vec3i branchOffset = getRandomValue(innerOffsets, context.random, nextForkPosOffset);
            if (branchOffset == null) continue;

            hasBranch = true;
            List<Vec3i> branchDirections = getOuterOffsets(forkDirection, branchOffset);
            Vec3i branchDirection = getRandomValue(branchDirections, context.random);
            if (branchDirection == null) continue;
            generateBranch(context, currentPos.add(branchOffset), branchDirection.subtract(branchOffset), leaves);
        }
    }

    private void generateBranch(TreeContext context, BlockPos pos, Vec3i direction, List<BlockPos> leaves) {
        int length = context.random.nextBetween(3, 4);

        for (int i = 0; i < length; i++) {
            placeWood(context, pos);
            pos = pos.add(direction);
            if (context.random.nextFloat() > 0.4f) pos = pos.up();
        }
        leaves.add(pos.up());
    }

    private void placeWood(TreeContext context, BlockPos pos) {
        getAndSetState(context.world, context.replacer, context.random, pos, context.config);
    }

    private static List<Vec3i> getInnerOffsets(Vec3i rootOffset) {
        List<Vec3i> result = new ObjectArrayList<>();
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                result.add(getOffset(rootOffset, x, y));
            }
        }
        return result;
    }

    private static List<Vec3i> getOuterOffsets(Vec3i rootOffset, Vec3i innerOffset) {
        Vec3i delta = innerOffset.multiply(2).subtract(rootOffset);
        return ObjectArrayList.of(innerOffset.add(delta.getX(), 0, 0), innerOffset.add(0, 0, delta.getZ()));
    }

    private static Vec3i getOffset(Vec3i rootOffset, int x, int z) {
        return new Vec3i(rootOffset.getX() * x, 0, rootOffset.getZ() * z);
    }

    @SafeVarargs
    @Nullable
    private static <T> T getRandomValue(List<T> list, Random random, T... except) {
        return getRandomValue(list, random, ObjectArrayList.of(except));
    }

    private static <T> T getRandomValue(List<T> list, Random random, List<T> except) {
        List<T> values = new ObjectArrayList<>(list);
        if (!except.isEmpty()) values.removeAll(except);
        if (values.isEmpty()) return null;
        return values.get(random.nextInt(values.size()));
    }

    private record TreeContext(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config) {
    }
}
