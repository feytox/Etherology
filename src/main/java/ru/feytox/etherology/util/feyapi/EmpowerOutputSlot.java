package ru.feytox.etherology.util.feyapi;

import net.minecraft.item.ItemStack;

public class EmpowerOutputSlot extends TrackedSlot {
    public EmpowerOutputSlot(UpdatableInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
