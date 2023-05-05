package ru.feytox.etherology.block.etherealGenerators;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.registry.item.EItems;

import static ru.feytox.etherology.block.etherealGenerators.AbstractEtherealGenerator.STALLED;

public class EtherealGeneratorDispenserBehavior extends ItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        BlockPos checkPos = pointer.getPos().add(direction.getVector());
        ServerWorld world = pointer.getWorld();

        if (world.getBlockEntity(checkPos) instanceof AbstractEtherealGeneratorBlockEntity generator) {
            BlockState state = generator.getCachedState();
            if (state.get(STALLED)) {
                stack.decrement(1);
                generator.unstall(world, state);
                return stack;
            }
        }

        return super.dispenseSilently(pointer, stack);
    }

    public static void register() {
        DispenserBlock.registerBehavior(EItems.ETHEREAL_OIL, new EtherealGeneratorDispenserBehavior());
    }
}
