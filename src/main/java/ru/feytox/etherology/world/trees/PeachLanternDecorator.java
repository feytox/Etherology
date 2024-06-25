package ru.feytox.etherology.world.trees;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import ru.feytox.etherology.block.forestLantern.ForestLanternBlock;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.world.TreesRegistry;

import java.util.List;
import java.util.stream.Collectors;

public class PeachLanternDecorator extends TreeDecorator {

    public static final PeachLanternDecorator INSTANCE = new PeachLanternDecorator();
    public static final Codec<PeachLanternDecorator> CODEC = Codec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreesRegistry.PEACH_LANTERN_DECORATOR;
    }

    @Override
    public void generate(Generator generator) {
        Random random = generator.getRandom();
        int count = random.nextBetween(0, 2);
        if (count == 0) return;

        int bottomY = generator.getLogPositions().get(0).getY();
        // TODO: 19.06.2024 consider to optimize and combine with same method in BirchBranchesDecorator
        List<LogSidePos> poses = generator.getLogPositions().stream()
                .filter(pos -> pos.getY()-bottomY < 6)
                .mapMulti((pos, consumer) -> {
                    consumer.accept(new LogSidePos(pos.north(), Direction.NORTH));
                    consumer.accept(new LogSidePos(pos.south(), Direction.SOUTH));
                    consumer.accept(new LogSidePos(pos.west(), Direction.WEST));
                    consumer.accept(new LogSidePos(pos.east(), Direction.EAST));
                })
                .map(obj -> ((LogSidePos) obj))
                .filter(logSidePos -> generator.isAir(logSidePos.pos))
                .collect(Collectors.toCollection(ObjectArrayList::new));
        if (poses.isEmpty()) return;

        for (int i = 0; i < count; i++) {
            LogSidePos logSidePos = poses.remove(random.nextInt(poses.size()));
            generator.replace(logSidePos.pos, DecoBlocks.FOREST_LANTERN.getDefaultState()
                    .with(ForestLanternBlock.FACING, logSidePos.direction));
        }
    }

    private record LogSidePos(BlockPos pos, Direction direction) {}
}
