package ru.feytox.etherology.blocks.etherWorkbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.ItemsRegistry;
import ru.feytox.etherology.recipes.ether.EtherRecipe;
import ru.feytox.etherology.util.feyapi.OutputSlot;
import ru.feytox.etherology.util.feyapi.SpecificSlot;
import ru.feytox.etherology.util.feyapi.UnspecificSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EtherWorkbenchScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ScreenHandlerContext context;

    public EtherWorkbenchScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public EtherWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        this(syncId, playerInventory, new SimpleInventory(14), context);
    }

    public EtherWorkbenchScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                       ScreenHandlerContext context) {
        super(Etherology.ETHER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 14);
        this.inventory = inventory;
        this.context = context;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        //Our inventory
        List<Item> shardItems = new ArrayList<>(List.of(ItemsRegistry.HEAVENLY_SHARD, ItemsRegistry.TERRESTRIAL_SHARD,
                ItemsRegistry.AQUATIC_SHARD, ItemsRegistry.DEEP_SHARD));
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new UnspecificSlot(inventory, shardItems, l + m * 3, 32 + l * 18, 17 + m * 18));
            }
        }
        // shards
        this.addSlot(new SpecificSlot(inventory, ItemsRegistry.HEAVENLY_SHARD, 9, 50, -5));
        this.addSlot(new SpecificSlot(inventory, ItemsRegistry.AQUATIC_SHARD, 10, 90, 35));
        this.addSlot(new SpecificSlot(inventory, ItemsRegistry.DEEP_SHARD, 11, 50, 75));
        this.addSlot(new SpecificSlot(inventory, ItemsRegistry.TERRESTRIAL_SHARD, 12, 10, 35));
        // output
        this.addSlot(new OutputSlot(inventory, 13, 142, 35));
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 99 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 157));
        }

        this.context.run(((world, pos) -> updateResult(this, world, playerInventory.player, pos)));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
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
    public void onContentChanged(Inventory inventory) {
        this.context.run(((world, pos) -> updateResult(this, world, this.player(), pos)));
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        this.context.run(((world, pos) -> updateResult(this, world, this.player(), pos)));
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private static void updateResult(ScreenHandler handler, World world, PlayerEntity player, BlockPos pos) {
        if (!world.isClient) {
            EtherWorkbenchBlockEntity block = (EtherWorkbenchBlockEntity) world.getBlockEntity(pos);
            if (block != null) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
                ItemStack result = ItemStack.EMPTY;
                Optional<EtherRecipe> match = world.getRecipeManager().getFirstMatch(EtherRecipe.Type.INSTANCE,
                        block, world);

                EtherRecipe recipe = null;
                if (match.isPresent()) {
                    recipe = match.get();
                    result = recipe.getOutput();
                }

                block.setStack(13, result);
                ((OutputSlot) handler.slots.get(13)).setRecipe(recipe);
                handler.setPreviousTrackedSlot(13, result);
                serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId,
                        handler.nextRevision(), 13, result));
            }
        }
    }
}
