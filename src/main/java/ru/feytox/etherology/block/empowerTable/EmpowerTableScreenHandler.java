package ru.feytox.etherology.block.empowerTable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import ru.feytox.etherology.item.PrimoShard;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.misc.*;

import static ru.feytox.etherology.registry.util.ScreenHandlersRegistry.EMPOWER_TABLE_SCREEN_HANDLER;

public class EmpowerTableScreenHandler extends ScreenHandler {
    private final PropertyDelegate propertyDelegate;
    private final UpdatableInventory inventory;

    public EmpowerTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleUpdatableInventory(10), new ArrayPropertyDelegate(4));
    }

    public EmpowerTableScreenHandler(int syncId, PlayerInventory playerInventory, UpdatableInventory inventory, PropertyDelegate propertyDelegate) {
        super(EMPOWER_TABLE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 10);
        checkDataCount(propertyDelegate, 4);
        this.propertyDelegate = propertyDelegate;
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addProperties(propertyDelegate);

        int m;
        int l;

        // grid
        int gridNum = 0;
        for (m = 0; m < 3; m++) {
            for (l = 0; l < 3; l++) {
                if ((m == 0 || m == 2) && (l == 0 || l == 2)) continue;
                this.addSlot(new TrackedSlot(inventory, gridNum, 27 + l * 19, 21 + m * 19));
                gridNum++;
            }
        }

        // rella
        this.addSlot(new TrackedPredictableSlot(inventory, 5, 23, 17, (stack) -> stack.isOf(EItems.PRIMOSHARD_RELLA)));
        // via
        this.addSlot(new TrackedPredictableSlot(inventory, 6, 69, 17, (stack) -> stack.isOf(EItems.PRIMOSHARD_VIA)));
        // clos
        this.addSlot(new TrackedPredictableSlot(inventory, 7, 23, 63, (stack) -> stack.isOf(EItems.PRIMOSHARD_CLOS)));
        // keta
        this.addSlot(new TrackedPredictableSlot(inventory, 8, 69, 63, (stack) -> stack.isOf(EItems.PRIMOSHARD_KETA)));

        // output
        this.addSlot(new OutputSlot(inventory, 9, 133, 40));

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 94 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 152));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (!slot.hasStack()) return newStack;

        boolean skip = false;
        ItemStack originalStack = slot.getStack();

        if (slot instanceof OutputSlot && !isFull(inventory.size(), slots.size())) {
            skip = true;
            newStack = originalStack.copy();
            inventory.onSpecialEvent(0, originalStack);
            if (!this.insertItem(originalStack, inventory.size(), slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }

        if (invSlot < inventory.size() && !skip) {
            skip = true;
            if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }

        if (originalStack.getItem() instanceof PrimoShard && !skip) {
            int primoSlot = 5;
            newStack = originalStack.copy();
            if (originalStack.isOf(EItems.PRIMOSHARD_VIA)) primoSlot = 6;
            if (originalStack.isOf(EItems.PRIMOSHARD_CLOS)) primoSlot = 7;
            if (originalStack.isOf(EItems.PRIMOSHARD_KETA)) primoSlot = 8;
            if (!this.insertItem(originalStack, primoSlot, primoSlot+1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (originalStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public int getRela() {
        return propertyDelegate.get(0);
    }

    public int getVia() {
        return propertyDelegate.get(1);
    }

    public int getClos() {
        return propertyDelegate.get(2);
    }

    public int getKeta() {
        return propertyDelegate.get(3);
    }

    public boolean shouldGlow() {
        return getRela() != 0 || getVia() != 0 || getClos() != 0 || getKeta() != 0;
    }

    public boolean isFull(int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            if (!slots.get(i).hasStack()) return false;
        }
        return true;
    }
}
