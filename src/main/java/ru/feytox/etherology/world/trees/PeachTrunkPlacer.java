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

import java.util.List;
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
        Placer placer = new Placer(world, replacer, random, height, startPos, config);
        BlockPos pos1 = generatePart(Direction.EAST, 1, placer);
        BlockPos pos2 = generatePart(Direction.NORTH, 2, placer);
        BlockPos pos3 = generatePart(Direction.WEST, 2, placer);
        BlockPos pos4 = generatePart(Direction.SOUTH, 1, placer);
        List<BlockPos> ends = List.of(pos1, pos2, pos3, pos4);

        ImmutableList.Builder<FoliagePlacer.TreeNode> builder = ImmutableList.builder();
        ends.forEach(blockPos -> builder.add(new FoliagePlacer.TreeNode(blockPos, 0, false)));
        return builder.build();
    }

    private BlockPos generatePart(Direction direction, int value, Placer placer) {
        Vec3i diffVec = direction.getVector().multiply(value);
        Vec3i branchVec = direction.getVector();
        BlockPos pos = placer.startPos.add(diffVec);
        setToDirt(placer.world, placer.replacer, placer.random, pos.down(), placer.config);
        int partHeight = (int) (placer.height * (0.4 + placer.random.nextDouble() * 0.6));
        for (int i = 0; i < partHeight; i++) {
            float percent = (float) i / partHeight;
            if (i == 0) {
                getAndSetState(placer, pos);
                pos = pos.subtract(branchVec);
                getAndSetState(placer, pos);
                continue;
            }
            if (percent < 0.5 || placer.random.nextDouble() > 0.75) {
                pos = pos.up();
                getAndSetState(placer, pos);
                continue;
            }

            pos = pos.up().add(branchVec);
            getAndSetState(placer, pos);
        }

        return pos.up(1);
    }

    private void getAndSetState(Placer placer, BlockPos pos) {
        this.getAndSetState(placer.world, placer.replacer, placer.random, pos, placer.config);
    }

    private double lenVec3i(Vec3i vec3i) {
        return Math.sqrt(vec3i.getX() * vec3i.getX() + vec3i.getY() * vec3i.getY() + vec3i.getZ() * vec3i.getZ());
    }

    private record Placer(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) { }
}
