package ru.feytox.etherology.block.jewelryTable;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.registry.util.ScreenHandlersRegistry;
import ru.feytox.etherology.util.feyapi.TrackedPredictableSlot;

public class JewelryTableScreenHandler extends ScreenHandler {

    @Getter
    private final JewelryTableInventory tableInv;
    private final ScreenHandlerContext context;

    public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public JewelryTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ScreenHandlersRegistry.JEWELRY_TABLE_SCREEN_HANDLER, syncId);
        tableInv = new JewelryTableInventory();
        this.context = context;
        tableInv.onOpen(playerInventory.player);

        // input lens
        this.addSlot(new TrackedPredictableSlot(tableInv, 0, 8, 17, stack -> stack.getItem() instanceof LensItem));

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
        if (id < 0 || id >= 152) return false;

        boolean isSoft = false;
        if (id >= 100) {
            isSoft = true;
            id -= 100;
        }

        boolean result = tableInv.markCell(isSoft, id);
        if (!result) return false;

        if (!isSoft) tableInv.updateCells(id);
        tableInv.markDirty();
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO: 21.01.2024 implement
        return null;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        context.run((world, pos) -> dropInventory(player, tableInv));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
