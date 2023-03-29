package ru.feytox.etherology.blocks.etherealFurnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import ru.feytox.etherology.util.feyapi.ClosedSlot;
import ru.feytox.etherology.util.feyapi.SpecificSlot;

import static ru.feytox.etherology.Etherology.ETHEREAL_FURNACE_SCREEN_HANDLER;

public class EtherealFurnaceScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public EtherealFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(3));
    }

    public EtherealFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ETHEREAL_FURNACE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 3);
        checkDataCount(propertyDelegate, 3);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);

        this.addProperties(propertyDelegate);

        // fuel slot
        this.addSlot(new SpecificSlot(inventory, Items.BLAZE_POWDER, 0, 8, 32));

        // item slot
        this.addSlot(new Slot(inventory, 1, 38, 35));

        // ether slot
        this.addSlot(new ClosedSlot(inventory, 2, 125, 35));

        int m;
        int l;
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
            newStack = originalStack.copy();
            if (!this.insertItem(originalStack, 1, 2, false)) {
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
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public boolean isCooking() {
        return propertyDelegate.get(1) != 0;
    }

    public float getFuelPercent() {
        return (float) propertyDelegate.get(0) / EtherealFurnaceBlockEntity.MAX_FUEL;
    }

    public float getCookingPercent() {
        return propertyDelegate.get(2) == 0 ? 0 : Math.min(1, (float) propertyDelegate.get(1) / propertyDelegate.get(2));
    }
}
