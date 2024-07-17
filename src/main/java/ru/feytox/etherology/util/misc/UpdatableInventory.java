package ru.feytox.etherology.util.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface UpdatableInventory extends InventoryRecipeInput {
    void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index);
    void onTrackedUpdate(int index);
    void onSpecialEvent(int eventId, ItemStack stack);
}
