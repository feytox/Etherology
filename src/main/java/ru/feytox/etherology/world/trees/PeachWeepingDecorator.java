package ru.feytox.etherology.world.trees;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.world.TreesRegistry;

public class PeachWeepingDecorator extends TreeDecorator {

    public static final PeachWeepingDecorator INSTANCE = new PeachWeepingDecorator();
    public static final MapCodec<PeachWeepingDecorator> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreesRegistry.PEACH_WEEPING_DECORATOR;
    }

    @Override
    public void generate(Generator generator) {
        val poses = new ObjectArrayList<>(generator.getLogPositions());
        Random random = generator.getRandom();
        int count = Math.min(random.nextBetween(0, 4), poses.size());
        if (count == 0) return;

        for (int i = 0; i < count; i++) {
            BlockPos logPos = poses.remove(random.nextInt(poses.size()));
            generator.replace(logPos, DecoBlocks.WEEPING_PEACH_LOG.getDefaultState());
        }
    }
}
