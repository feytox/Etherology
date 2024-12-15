package ru.feytox.etherology.block.shelf;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import ru.feytox.etherology.block.furniture.FurnitureData;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;

public class ShelfData extends FurnitureData implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public ShelfData(boolean isBottom) {
        super(isBottom);
    }

    @Override
    public void onUse(World world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {
        if (hand.equals(Hand.OFF_HAND) || !(world instanceof ServerWorld serverWorld))
            return;

        boolean isLeft = hitPos.x < 0.5;
        int slot = isLeft ? 0 : 1;
        ItemStack currentStack = getStack(slot);
        ItemStack playerStack = player.getStackInHand(hand);
        boolean isSameItem = playerStack.isOf(currentStack.getItem());

        if (!isSameItem && !playerStack.isEmpty()) {
            // замена предмета на предмет
            ItemStack takingStack = playerStack.copy();
            playerStack = currentStack;
            currentStack = takingStack;

            player.setStackInHand(hand, playerStack);
            setStack(slot, currentStack);
            updateData(serverWorld, pos);
            PedestalBlockEntity.playItemPlaceSound(serverWorld, pos);

        } else if (!currentStack.isEmpty() && !playerStack.isEmpty()) {
            // кладём предмет на НЕПУСТУЮ полку
            ItemStack takingStack = playerStack.copy();
            takingStack.setCount(currentStack.getMaxCount() - currentStack.getCount());

            playerStack.decrement(takingStack.getCount());
            currentStack.increment(takingStack.getCount());
            updateData(serverWorld, pos);
            PedestalBlockEntity.playItemPlaceSound(serverWorld, pos);

        } else if (!currentStack.isEmpty()) {
            // берём предмет ПУСТОЙ рукой с НЕПУСТОЙ полки
            ItemStack takingStack = currentStack.copy();
            currentStack.setCount(0);

            player.setStackInHand(hand, takingStack);
            updateData(serverWorld, pos);
            PedestalBlockEntity.playItemTakeSound(serverWorld, pos);
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbtCompound, inventory, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registryLookup) {
        inventory.clear();
        Inventories.readNbt(nbtCompound, inventory, registryLookup);
    }
}
