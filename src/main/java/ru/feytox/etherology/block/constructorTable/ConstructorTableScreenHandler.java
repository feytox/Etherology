package ru.feytox.etherology.block.constructorTable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.util.ScreenHandlersRegistry;
import ru.feytox.etherology.util.feyapi.SpecificSlot;

public class ConstructorTableScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public ConstructorTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public ConstructorTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlersRegistry.CONSTRUCTOR_TABLE_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        inventory.onOpen(playerInventory.player);
        this.inventory = inventory;

        this.addSlot(new SpecificSlot(inventory, ToolItems.ETHER_STAFF, 0, 8, 16));

        int m;
        int l;
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 54 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 112));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO: 08.09.2023 add quick moving
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
