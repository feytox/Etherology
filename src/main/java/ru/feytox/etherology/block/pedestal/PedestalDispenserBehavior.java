package ru.feytox.etherology.block.pedestal;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PedestalDispenserBehavior extends ItemDispenserBehavior {

    @Getter(lazy = true)
    private static final PedestalDispenserBehavior instance = new PedestalDispenserBehavior();

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        return tryUseOnPedestal(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
    }

    public static boolean testDispenser(BlockPointer pointer, ItemStack stack) {
        if (stack.isEmpty()) return false;
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        BlockPos checkPos = pointer.getPos().add(direction.getVector());
        ServerWorld world = pointer.getWorld();

        return world.getBlockEntity(checkPos) instanceof PedestalBlockEntity;
    }

    private boolean tryUseOnPedestal(BlockPointer pointer, ItemStack stack) {
        if (stack.isEmpty()) return false;
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        BlockPos checkPos = pointer.getPos().add(direction.getVector());
        ServerWorld world = pointer.getWorld();

        if (!(world.getBlockEntity(checkPos) instanceof PedestalBlockEntity pedestal)) return false;

        BlockState state = world.getBlockState(checkPos);
        ItemStack pedestalStack = pedestal.getStack(0);
        ItemStack carpetStack = pedestal.getStack(1);

        boolean isCarpetPlaced = placeCarpet(world, pedestal, state, carpetStack, stack, direction);
        boolean isSomethingPlaced = isCarpetPlaced || placeItem(world, pedestal, checkPos, pedestalStack, stack);

        if (!isSomethingPlaced) return false;
        pedestal.syncData(world);
        return true;
    }

    private boolean placeCarpet(ServerWorld world, PedestalBlockEntity pedestal, BlockState state, ItemStack carpetStack, ItemStack testStack, Direction facing) {
        if (!carpetStack.isEmpty()) return false;
        if (!(testStack.getItem() instanceof BlockItem blockItem)) return false;
        if (!(blockItem.getBlock() instanceof DyedCarpetBlock carpet)) return false;

        pedestal.setStack(1, testStack.copyWithCount(1));
        testStack.decrement(1);
        pedestal.setCarpetColor(world, facing.getOpposite(), state, carpet.getDyeColor(), true);
        return true;
    }

    private boolean placeItem(ServerWorld world, PedestalBlockEntity pedestal, BlockPos pedestalPos, ItemStack pedestalStack, ItemStack testStack) {
        if (!pedestalStack.isEmpty()) return false;

        pedestal.setStack(0, testStack.copyWithCount(1));
        testStack.decrement(1);
        PedestalBlockEntity.playItemPlaceSound(world, pedestalPos);
        return true;
    }
}
