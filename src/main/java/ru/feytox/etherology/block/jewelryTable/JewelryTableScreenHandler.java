package ru.feytox.etherology.block.jewelryTable;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.registry.util.ScreenHandlersRegistry;
import ru.feytox.etherology.util.feyapi.PredictableSlot;

@Getter
public class JewelryTableScreenHandler extends ScreenHandler {

    private final JewelryTableInventory tableInv;

    public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new JewelryTableInventory());
    }

    public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, JewelryTableInventory tableInventory) {
        super(ScreenHandlersRegistry.JEWELRY_TABLE_SCREEN_HANDLER, syncId);
        tableInv = tableInventory;
        tableInv.onOpen(playerInventory.player);

        // input lens
        this.addSlot(new PredictableSlot(tableInv, 0, 8, 17, stack -> stack instanceof LensItem));

        int m;
        int l;
        //The player inv
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 121 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 179));
        }

        tableInv.markDirty();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id < 0 || id >= 164) return false;

        boolean isSoft = false;
        if (id >= 100) {
            isSoft = true;
            id -= 100;
        }

        boolean result = tableInv.markCell(isSoft, id);
        if (!result) return false;

        boolean broken = tableInv.damageLens(isSoft ? 1 : 2);
        if (!isSoft && !broken) tableInv.updateCells(id);

        if (player.getWorld() instanceof ServerWorld serverWorld) {
            tableInv.updateRecipe(serverWorld);
        }

        tableInv.markDirty();
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(invSlot);
        if (!slot.hasStack()) return newStack;

        ItemStack originalStack = slot.getStack();
        if (!(originalStack.getItem() instanceof LensItem)) return newStack;

        newStack = originalStack.copy();
        if (invSlot < tableInv.size()) {
            if (!this.insertItem(originalStack, tableInv.size(), slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.insertItem(originalStack, 0, tableInv.size(), false)) {
            return ItemStack.EMPTY;
        }

        if (originalStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void close(PlayerEntity player) {
        tableInv.onClose(player);
        super.close(player);
    }
}
