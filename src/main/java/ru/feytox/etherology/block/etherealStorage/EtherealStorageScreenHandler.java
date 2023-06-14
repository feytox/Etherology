package ru.feytox.etherology.block.etherealStorage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.item.glints.AbstractGlintItem;
import ru.feytox.etherology.util.feyapi.ClosedSlot;
import ru.feytox.etherology.util.feyapi.TypedSlot;

public class EtherealStorageScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public EtherealStorageScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(4));
    }

    public EtherealStorageScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Etherology.ETHEREAL_STORAGE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 4);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        //Glints inventory
        for (m = 0; m < 3; ++m) {
            this.addSlot(new TypedSlot<>(AbstractGlintItem.class, inventory, m, 79 + m * 19, 35));
        }
        //Ether inventory
        this.addSlot(new ClosedSlot(this.inventory, 3, 35, 35));

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 69 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 127));
        }
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            if (!(originalStack.getItem() instanceof AbstractGlintItem)) return newStack;

            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public void close(PlayerEntity player) {
        inventory.onClose(player);
        super.close(player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
