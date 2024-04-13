package ru.feytox.etherology.util.misc;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SpecificSlot extends Slot {
    private final ItemStack specificItem;

    public SpecificSlot(Inventory inventory, Item specificItem, int index, int x, int y) {
        super(inventory, index, x, y);
        this.specificItem = specificItem.getDefaultStack();
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isItemEqual(specificItem);
    }
}
