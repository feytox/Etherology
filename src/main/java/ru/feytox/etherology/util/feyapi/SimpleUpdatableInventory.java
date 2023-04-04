package ru.feytox.etherology.util.feyapi;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class SimpleUpdatableInventory implements UpdatableInventory {
    private final DefaultedList<ItemStack> stacks;

    public SimpleUpdatableInventory(int size) {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {}

    @Override
    public void onTrackedUpdate(int index) {}

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }
}
