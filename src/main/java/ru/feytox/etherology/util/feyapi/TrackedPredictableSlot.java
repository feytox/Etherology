package ru.feytox.etherology.util.feyapi;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class TrackedPredictableSlot extends TrackedSlot {
    private final Predicate<ItemStack> predicate;

    public TrackedPredictableSlot(UpdatableInventory inventory, int index, int x, int y, Predicate<ItemStack> predicate) {
        super(inventory, index, x, y);
        this.predicate = predicate;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return predicate.test(stack);
    }
}
