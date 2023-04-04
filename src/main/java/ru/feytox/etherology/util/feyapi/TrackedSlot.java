package ru.feytox.etherology.util.feyapi;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class TrackedSlot extends Slot {
    public TrackedSlot(UpdatableInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        super.onTakeItem(player, stack);
        if (inventory instanceof UpdatableInventory updatableInventory) {
            updatableInventory.onTrackedSlotTake(player, stack, getIndex());
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (inventory instanceof UpdatableInventory updatableInventory) {
            updatableInventory.onTrackedUpdate(getIndex());
        }
    }
}
