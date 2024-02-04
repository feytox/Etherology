package ru.feytox.etherology.util.feyapi;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class PredictableSlot extends Slot {
    private final Predicate<Item> predicate;

    public PredictableSlot(Inventory inventory, int index, int x, int y, Predicate<Item> predicate) {
        super(inventory, index, x, y);
        this.predicate = predicate;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return predicate.test(stack.getItem());
    }
}
