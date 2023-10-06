package ru.feytox.etherology.block.constructorTable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import ru.feytox.etherology.block.empowerTable.PartsInventory;
import ru.feytox.etherology.item.EtherStaff;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.util.ScreenHandlersRegistry;
import ru.feytox.etherology.util.feyapi.SpecificSlot;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConstructorTableScreenHandler extends ScreenHandler {
    private final Inventory staffInv;
    private final PartsInventory partsInv;
    private final ScreenHandlerContext context;

    public ConstructorTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), ScreenHandlerContext.EMPTY);
    }

    public ConstructorTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory staffInv, ScreenHandlerContext context) {
        super(ScreenHandlersRegistry.CONSTRUCTOR_TABLE_SCREEN_HANDLER, syncId);
        checkSize(staffInv, 1);
        staffInv.onOpen(playerInventory.player);
        this.staffInv = staffInv;
        this.context = context;
        partsInv = loadParts(getStaffStack(), new PartsInventory(this, 6));

        this.addSlot(new SpecificSlot(staffInv, ToolItems.ETHER_STAFF, 0, 29, 13));

        for (int i = 0; i <= 1; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SpecificSlot(partsInv, ToolItems.ETHER_STAFF, j + i * 3, 10 + 38 * i, 36 + 21 * j));
            }
        }

        int m;
        int l;
        //The player staffInv
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 115 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 173));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        ItemStack staffStack = getStaffStack();
        boolean isStaff = !staffStack.isEmpty() && staffStack.isOf(ToolItems.ETHER_STAFF);

        // part change
        if (inventory instanceof PartsInventory parts) {
            if (!isStaff) return;

            this.context.run((world, pos) -> {
                combineParts(staffStack, parts.getStacks());
                staffInv.markDirty();
            });
            return;
        }

        if (!(inventory instanceof ConstructorTableBlockEntity)) return;
        this.context.run(((world, pos) -> {
            // staff replace/place
            if (isStaff) {
                partsInv.clear();
                loadParts(staffStack, partsInv);
                partsInv.markDirty();
                return;
            }

            // staff remove
            partsInv.clear();
            partsInv.markDirty();
        }));
    }

    public ItemStack getStaffStack() {
        return staffInv.getStack(0);
    }

    private void combineParts(ItemStack staff, List<ItemStack> parts) {
        Map<StaffPart, StaffPartInfo> partsMap = new Object2ObjectOpenHashMap<>();
        parts.stream()
                .map(EtherStaff::readNbt)
                .filter(Objects::nonNull)
                .forEach(partsMap::putAll);

        EtherStaff.writeNbt(staff, partsMap);
    }

    private PartsInventory loadParts(ItemStack staff, PartsInventory partsInventory) {
        Map<StaffPart, StaffPartInfo> parts = EtherStaff.readNbt(staff);
        if (parts == null) return partsInventory;

        parts.values().stream()
                .sorted(Comparator.comparing(partInfo -> partInfo.getPart().ordinal()))
                .map(partInfo -> {
                    ItemStack partStack = new ItemStack(ToolItems.ETHER_STAFF);
                    EtherStaff.writeNbt(partStack, Map.of(partInfo.getPart(), partInfo));
                    return partStack;
                })
                .forEach(partsInventory::addStack);
        return partsInventory;
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
