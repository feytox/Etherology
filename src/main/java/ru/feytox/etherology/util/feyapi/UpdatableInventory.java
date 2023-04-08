package ru.feytox.etherology.util.feyapi;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface UpdatableInventory extends ImplementedInventory {
    void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index);
    void onTrackedUpdate(int index);
    void onSpecialEvent(int eventId, ItemStack stack);
}
