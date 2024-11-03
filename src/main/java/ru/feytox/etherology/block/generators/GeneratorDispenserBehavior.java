package ru.feytox.etherology.block.generators;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static ru.feytox.etherology.block.generators.AbstractGenerator.STALLED;

public class GeneratorDispenserBehavior extends ItemDispenserBehavior {

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        BlockPos checkPos = pointer.pos().add(direction.getVector());
        ServerWorld world = pointer.world();

        if (!(world.getBlockEntity(checkPos) instanceof AbstractGeneratorBlockEntity generator)) return super.dispenseSilently(pointer, stack);

        BlockState state = generator.getCachedState();
        if (state.get(STALLED)) {
            stack.decrement(1);
            generator.unstall(world, state);
            return stack;
        }

        return super.dispenseSilently(pointer, stack);
    }
}
