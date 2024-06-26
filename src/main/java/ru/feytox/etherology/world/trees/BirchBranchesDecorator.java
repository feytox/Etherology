package ru.feytox.etherology.world.trees;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import ru.feytox.etherology.registry.world.TreesRegistry;

import java.util.Collections;
import java.util.List;

public class BirchBranchesDecorator extends TreeDecorator {

    public static final BirchBranchesDecorator INSTANCE = new BirchBranchesDecorator();
    public static final Codec<BirchBranchesDecorator> CODEC = Codec.unit(() -> INSTANCE);

    private static final Direction[] OFFSETS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreesRegistry.BIRCH_BRANCHES_DECORATOR;
    }

    @Override
    public void generate(Generator generator) {
        List<Direction> offsets = new ObjectArrayList<>(OFFSETS);
        Collections.shuffle(offsets);
        List<BlockPos> logs = generator.getLogPositions();
        logs = logs.subList(1, logs.size()-3);

        int branchesCount = 0;
        float chance = 1.0f / logs.size();
        for (BlockPos logPos : logs) {
            if (branchesCount >= 2) break;
            if (generator.getRandom().nextFloat() > chance) continue;
            Direction direction = offsets.get(branchesCount % 4);
            BlockPos pos = logPos.add(direction.getVector());
            if (!generator.isAir(pos)) continue;

            generator.replace(pos, Blocks.BIRCH_LOG.getDefaultState().with(PillarBlock.AXIS, direction.getAxis()));
            branchesCount += 1;
        }
    }
}
