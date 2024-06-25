package ru.feytox.etherology.world.trees;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import ru.feytox.etherology.registry.world.TreesRegistry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BirchBranchesDecorator extends TreeDecorator {

    public static final BirchBranchesDecorator INSTANCE = new BirchBranchesDecorator();
    public static final Codec<BirchBranchesDecorator> CODEC = Codec.unit(() -> INSTANCE);

    private static final float HIGH_CHANCE = 0.97f;
    private static final float LOW_CHANCE = 0.08f;

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreesRegistry.BIRCH_BRANCHES_DECORATOR;
    }

    @Override
    public void generate(Generator generator) {
        Random random = generator.getRandom();
        int count = random.nextBetween(0, 2);
        if (count == 0) return;

        int bottomY = generator.getLogPositions().get(0).getY();
        // TODO: 25.06.2024 consider to optimize and combine with same method in PeachLanternDecorator
        List<LogSidePos> poses = generator.getLogPositions().stream()
                .filter(pos -> pos.getY()-bottomY > 1)
                .mapMulti((pos, consumer) -> {
                    consumer.accept(new LogSidePos(pos.north(), Direction.NORTH));
                    consumer.accept(new LogSidePos(pos.south(), Direction.SOUTH));
                    consumer.accept(new LogSidePos(pos.west(), Direction.WEST));
                    consumer.accept(new LogSidePos(pos.east(), Direction.EAST));
                })
                .map(obj -> ((LogSidePos) obj))
                .filter(logSidePos -> generator.isAir(logSidePos.pos))
                .collect(Collectors.toCollection(ObjectArrayList::new));

        Set<Direction> usedDirections = new ObjectOpenHashSet<>(count);
        for (int i = 0; i < count; i++) {
            if (poses.isEmpty()) return;
            LogSidePos logSidePos = poses.remove(random.nextInt(poses.size()));
            if (usedDirections.contains(logSidePos.direction)) {
                i -= 1;
                continue;
            }
            usedDirections.add(logSidePos.direction);

            generator.replace(logSidePos.pos, Blocks.BIRCH_LOG.getDefaultState());
            Vec3i trunkOffset = logSidePos.direction.getOpposite().getVector();
            placeLeaves(generator, logSidePos.pos.down(), trunkOffset, 1, LOW_CHANCE, 0);
            placeLeaves(generator, logSidePos.pos, trunkOffset, 0, 1, HIGH_CHANCE);
            placeLeaves(generator, logSidePos.pos.up(), trunkOffset, 1, HIGH_CHANCE, LOW_CHANCE);
        }
    }

    /**
     * @see PeachFoliagePlacer
     */
    private void placeLeaves(Generator generator, BlockPos center, Vec3i trunkOffset, float centerChance, float mainChance, float cornerChance) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (trunkOffset.getX() != 0 && dx == trunkOffset.getX() || trunkOffset.getZ() != 0 && dz == trunkOffset.getZ()) continue;
                float chance = dx != 0 && dz != 0 ? cornerChance : mainChance;
                if (dx == 0 && dz == 0) chance = centerChance;
                if (chance == 0 || (chance != 1 && generator.getRandom().nextFloat() > chance)) continue;
                generator.replace(center.add(dx, 0, dz), Blocks.BIRCH_LEAVES.getDefaultState());
            }
        }
    }

    private record LogSidePos(BlockPos pos, Direction direction) {}
}
