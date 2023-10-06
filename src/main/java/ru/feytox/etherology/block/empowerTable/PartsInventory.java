package ru.feytox.etherology.block.empowerTable;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.block.constructorTable.ConstructorTableScreenHandler;

import java.util.List;

public class PartsInventory extends SimpleInventory {

    private final ConstructorTableScreenHandler handler;

    public PartsInventory(ConstructorTableScreenHandler handler, int size) {
        super(size);
        this.handler = handler;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        handler.onContentChanged(this);
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }
}
