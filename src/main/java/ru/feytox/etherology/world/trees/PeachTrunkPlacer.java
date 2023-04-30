package ru.feytox.etherology.world.trees;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
        Placer placer = new Placer(world, replacer, random, height, startPos, config, 4, Direction.UP);
        List<BlockPos> ends = generateTree(placer);

        ImmutableList.Builder<FoliagePlacer.TreeNode> builder = ImmutableList.builder();
        ends.forEach(blockPos -> builder.add(new FoliagePlacer.TreeNode(blockPos, 0, false)));
        return builder.build();
    }

    private List<BlockPos> generateTree(Placer placer) {
        List<Integer> variants = new ArrayList<>(List.of(0, 1, 2, 3));
        List<BlockPos> ends = new ArrayList<>();
        int childCount = MathHelper.clamp(placer.childCount, 1, 4);


        for (int i = 0; i < childCount; i++) {
            int num = variants.remove(placer.random.nextBetween(0, variants.size()-1));
            switch (num) {
                case 0 -> ends.addAll(generatePart(placer.startPos, Direction.SOUTH, Direction.WEST, placer));
                case 1 -> ends.addAll(generatePart(placer.startPos.north(), Direction.NORTH, Direction.WEST, placer));
                case 2 -> ends.addAll(generatePart(placer.startPos.east(), Direction.EAST, Direction.SOUTH, placer));
                case 3 -> ends.addAll(generatePart(placer.startPos.north().east(), Direction.NORTH, Direction.EAST, placer));
            }
        }

        return ends;
    }

    private List<BlockPos> generatePart(BlockPos pos, Direction branch1, Direction branch2, Placer placer) {
        // TODO: 30/04/2023 add setToDirt or etc
        Function<Integer, Direction> branchDir = num -> num == 1 ? branch1 : branch2;
        if (!placer.treeDirection.equals(Direction.UP)) {
            Direction treeDirection = placer.treeDirection;
            branchDir = num -> treeDirection;
        }

        int partHeight = (int) (placer.height * (0.6 + placer.random.nextDouble() * 0.7));
        List<BlockPos> ends = new ArrayList<>();
        for (int i = 0; i < partHeight; i++) {
            float percent = (float) i / partHeight;
            getAndSetState(placer, pos);
            if (placer.childCount == 4 && i == 0) {
                for (int j = 1; j < placer.random.nextBetween(1, 2); j++) {
                    BlockPos basePos = pos.add(branchDir.apply(j).getVector());
                    getAndSetState(placer, basePos);
                    if (placer.random.nextDouble() < 0.3) getAndSetState(placer, basePos.up());
                }
                pos = pos.up();
                continue;
            }
            if (percent > 0.5 && placer.random.nextDouble() < 0.6) {
                Direction treeDirection = branchDir.apply(placer.random.nextBetween(1, 2));
                if (placer.childCount == 1) {
                    pos = pos.up().add(treeDirection.getVector());
                    continue;
                }

                placer = placer.with(pos.add(treeDirection.getVector())).with(treeDirection)
                        .with(partHeight-i, placer.childCount-2);
                ends.addAll(generateTree(placer));
                break;
            }
            pos = pos.up();
        }
        if (ends.isEmpty()) ends.add(pos.up());

        return ends;
    }

    private void getAndSetState(Placer placer, BlockPos pos) {
        this.getAndSetState(placer.world, placer.replacer, placer.random, pos, placer.config);
    }

    private record Placer(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config, int childCount, Direction treeDirection) {
        public Placer with(BlockPos pos) {
            return new Placer(world, replacer, random, height, pos, config, childCount, treeDirection);
        }

        public Placer with(int newHeight, int newChildCount) {
            return new Placer(world, replacer, random, newHeight, startPos, config, newChildCount, treeDirection);
        }

        public Placer with(Direction direction) {
            return new Placer(world, replacer, random, height, startPos, config, childCount, direction);
        }
    }
}
