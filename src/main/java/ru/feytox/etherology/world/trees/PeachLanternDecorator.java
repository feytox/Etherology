package ru.feytox.etherology.world.trees;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
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
import java.util.stream.Stream;

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
        // TODO: 19.06.2024 consider to optimize
        LongOpenHashSet leavesAndLogs = Stream.concat(generator.getLeavesPositions().stream(), generator.getLogPositions().stream())
                .map(BlockPos::asLong).collect(Collectors.toCollection(LongOpenHashSet::new));
        List<LanternPos> poses = generator.getLogPositions().stream()
                .filter(pos -> pos.getY()-bottomY < 6)
                .mapMulti((pos, consumer) -> {
                    consumer.accept(new LanternPos(pos.north(), Direction.NORTH));
                    consumer.accept(new LanternPos(pos.south(), Direction.SOUTH));
                    consumer.accept(new LanternPos(pos.west(), Direction.WEST));
                    consumer.accept(new LanternPos(pos.east(), Direction.EAST));
                })
                .map(obj -> ((LanternPos) obj))
                .filter(lanternPos -> !leavesAndLogs.contains(lanternPos.pos.asLong()))
                .collect(Collectors.toCollection(ObjectArrayList::new));

        for (int i = 0; i < count; i++) {
            LanternPos lanternPos = poses.remove(random.nextInt(poses.size()));
            generator.replace(lanternPos.pos, DecoBlocks.FOREST_LANTERN.getDefaultState()
                    .with(ForestLanternBlock.FACING, lanternPos.direction));
        }
    }

    private record LanternPos(BlockPos pos, Direction direction) {}
}
