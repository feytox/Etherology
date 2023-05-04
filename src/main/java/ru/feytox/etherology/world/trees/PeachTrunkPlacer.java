package ru.feytox.etherology.world.trees;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
        Placer placer = new Placer(world, replacer, random, config);
        Map<Integer, List<BlockPos>> allEnds = generateNode(placer, startPos, Part.getRandomPart(random), 4, height, 0);
        List<BlockPos> ends = new ArrayList<>();
        allEnds.forEach((i, b) -> ends.addAll(b));

        ImmutableList.Builder<FoliagePlacer.TreeNode> builder = ImmutableList.builder();
        ends.forEach(blockPos -> builder.add(new FoliagePlacer.TreeNode(blockPos, 0, false)));
        return builder.build();
    }

    private Map<Integer, List<BlockPos>> generateNode(Placer placer, BlockPos startPos, Part startPart, int partCount, int height, int gen) {
        // TODO: 30/04/2023 add setToDirt or etc
        List<Part> parts = Part.getParts(placer.random, startPart, partCount);
        List<Direction> bannedDirections = partCount == 4 ? new ArrayList<>() : startPart.getBannedDirection();
        Map<Integer, List<BlockPos>> ends = new HashMap<>();
        if (partCount < 1) return ends;
        for (Part part : parts) {
            int partHeight = partCount == 1 ? Math.min(3, height) : (int) (height * 0.8 * (1 + placer.random.nextDouble()));
            BlockPos partPos = startPart.getOtherPartPos(startPos, part);
            boolean withSubNodes = false;
            for (int i = 0; i < partHeight; i++) {
                float percent = (float) i / partHeight;
                getAndSetState(placer, partPos);
                if (gen == 0 && i == 0) {
                    for (int j = 0; j < placer.random.nextBetween(0, 1); j++) {
                        BlockPos basePos = partPos.add(part.getBranchVec(placer.random));
                        getAndSetState(placer, basePos);
                        if (placer.random.nextDouble() < 0.3) getAndSetState(placer, basePos.up());
                    }
                    partPos = partPos.up();
                    continue;
                }

                Direction branch = part.getBranch(placer.random);
                if (placer.random.nextDouble() < 0.3 * percent * 10 && !bannedDirections.contains(branch)) {
                    partPos = partPos.add(branch.getVector());
                    if (partCount == 1) {
                        if (placer.random.nextDouble() < 0.75) break;
                        partPos = partPos.up();
                        continue;
                    }

                    int d = percent > 0.7 ? 2 : 1;
                    Map<Integer, List<BlockPos>> subNodes = generateNode(placer, partPos, part,
                            partCount - d,
                            partHeight - i, gen+1);
                    subNodes.forEach((genI, genPos) -> {
                        List<BlockPos> poses = ends.getOrDefault(genI, new ArrayList<>());
                        poses.addAll(genPos);
                        ends.put(genI, poses);
                    });
                    withSubNodes = true;
                    break;
                }
                partPos = partPos.up();
                if (percent > 0.7 && placer.random.nextDouble() < 0.2) break;
            }
            if (!withSubNodes) {
                List<BlockPos> poses = ends.getOrDefault(gen, new ArrayList<>());
                poses.add(partPos.up());
                ends.put(gen, poses);
            }
        }

        return ends;
    }

    private void getAndSetState(Placer placer, BlockPos pos) {
        this.getAndSetState(placer.world, placer.replacer, placer.random, pos, placer.config);
    }

    private record Placer(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config) {
    }

    private enum Part {
        TOP_LEFT(v -> v, Direction.NORTH, Direction.WEST),
        TOP_RIGHT(Vec3i::east, Direction.NORTH, Direction.EAST),
        BOTTOM_LEFT(Vec3i::south, Direction.SOUTH, Direction.WEST),
        BOTTOM_RIGHT(v -> v.south().east(), Direction.SOUTH, Direction.EAST);

        private final PartVector partVector;
        private final Direction branch1;
        private final Direction branch2;

        Part(PartVector partVector, Direction branch1, Direction branch2) {
            this.partVector = partVector;
            this.branch1 = branch1;
            this.branch2 = branch2;
        }

        private Vec3i getVector() {
            return partVector.getPartPos(Vec3i.ZERO);
        }

        public BlockPos getOtherPartPos(BlockPos partPos, Part otherPart) {
            return partPos.subtract(getVector()).add(otherPart.getVector());
        }

        public static Part getRandomPart(Random random) {
            return getRandomPart(random, new ArrayList<>());
        }

        @Nullable
        public static Part getRandomPart(Random random, List<Part> except) {
            int rIndex = random.nextBetween(1, 4 - except.size());
            int index = 0;
            for (int i = 0; i < values().length; i++) {
                Part part = values()[i];
                if (!except.contains(part)) index++;
                if (index == rIndex) return part;
            }
            return null;
        }

        public static List<Part> getParts(Random random, Part startPart, int count) {
            if (count <= 0 || count > 4) return new ArrayList<>();
            if (count == 4) return List.of(values());
            List<Part> result = new ArrayList<>(List.of(startPart));
            for (int i = 0; i < count-1; i++) {
                result.add(getRandomPart(random, result));
            }
            return result;
        }


        public Vec3i getBranchVec(Random random) {
            return getBranch(random).getVector();
        }

        public Direction getBranch(Random random) {
            return random.nextBoolean() ? branch1 : branch2;
        }

        public List<Direction> getBannedDirection() {
            return List.of(branch1.getOpposite(), branch2.getOpposite());
        }
    }

    protected interface PartVector {
        Vec3i getPartPos(Vec3i startPos);
    }
}
