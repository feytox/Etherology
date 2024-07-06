package ru.feytox.etherology.block.inventorTable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.PatternTabletItem;
import ru.feytox.etherology.magic.staff.StaffMetals;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ScreenHandlersRegistry;
import ru.feytox.etherology.util.misc.OutputSlot;
import ru.feytox.etherology.util.misc.TrackedPredictableSlot;
import ru.feytox.etherology.util.misc.TrackedSlot;

import java.util.List;

public class InventorTableScreenHandler extends ScreenHandler {

    @Getter
    private final InventorTableInventory tableInv;
    private final ScreenHandlerContext context;

    public InventorTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public InventorTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ScreenHandlersRegistry.INVENTOR_TABLE_SCREEN_HANDLER, syncId);
        tableInv = new InventorTableInventory();
        this.context = context;
        tableInv.onOpen(playerInventory.player);

        // input staff
        this.addSlot(new TrackedPredictableSlot(tableInv, 0, 8, 13, stack -> stack.isOf(ToolItems.STAFF)));
        // pattern item
        this.addSlot(new TrackedPredictableSlot(tableInv, 1, 28, 13, stack -> stack.getItem() instanceof PatternTabletItem));
        // item for pattern apply
        this.addSlot(new TrackedSlot(tableInv, 2, 48, 13));
        // output staff
        this.addSlot(new OutputSlot(tableInv, 3, 148, 36));

        int m;
        int l;
        //The player inv
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 90 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 148));
        }

        tableInv.setSelectedPart(null);
        tableInv.updateResult();
        tableInv.markDirty();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        context.run((world, pos) -> {
            tableInv.setStack(3, ItemStack.EMPTY);
            dropInventory(player, tableInv);
        });
    }

    public List<StaffPart> getStaffParts() {
        ItemStack inputStaff = tableInv.getStack(0);
        ItemStack patternItem = tableInv.getStack(1);
        ItemStack itemForPattern = tableInv.getStack(2);
        StaffMetals patternMetal = StaffMetals.getFromStack(itemForPattern);

        if (!inputStaff.isOf(ToolItems.STAFF) || !(patternItem.getItem() instanceof PatternTabletItem) || patternMetal == null) {
            return new ObjectArrayList<>();
        }

        return ObjectArrayList.of(StaffPart.HEAD, StaffPart.DECOR, StaffPart.TIP);
    }

    @Nullable
    public StaffPart getSelectedPart() {
        return tableInv.getSelectedPart();
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        List<StaffPart> staffParts = getStaffParts();
        if (id < 0 || id >= staffParts.size()) return false;

        tableInv.setSelectedPart(staffParts.get(id));
        tableInv.updateResult();
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        // click on empty slot
        if (!slot.hasStack()) return newStack;

        ItemStack originalStack = slot.getStack();
        newStack = originalStack.copy();

        // from table to player
        if (invSlot < tableInv.size()) {
            if (insertItem(originalStack, tableInv.size(), slots.size(), true)) {
                tableInv.onTrackedSlotTake(player, originalStack, invSlot);
            } else {
                return ItemStack.EMPTY;
            }
        }
        // from player to table
        else {
            if (originalStack.isOf(ToolItems.STAFF)) {
                if (!insertItem(originalStack, 0, 1, false)) return ItemStack.EMPTY;
            } else if (originalStack.getItem() instanceof PatternTabletItem) {
                if (!insertItem(originalStack, 1, 2, false)) return ItemStack.EMPTY;
            } else if (!insertItem(originalStack, 2, 3, false)) return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) {
            slot.setStackNoCallbacks(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
