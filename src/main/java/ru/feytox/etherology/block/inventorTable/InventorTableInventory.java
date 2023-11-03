package ru.feytox.etherology.block.inventorTable;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.item.PatternTabletItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.staff.StaffMetals;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffStyles;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.feyapi.UpdatableInventory;

public class InventorTableInventory implements UpdatableInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);

    @Nullable @Getter @Setter
    private StaffPart selectedPart = null;

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {
        if (index != 3) return;
        // take output staff
        setStack(0, ItemStack.EMPTY);
        getStack(1).decrement(1);
        getStack(2).decrement(1);

        markDirty();

        if (!(player instanceof ClientPlayerEntity)) return;
        player.playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 1.0f, player.getWorld().random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void onTrackedUpdate(int index) {
        if (index == 3) return;

        updateResult();
        markDirty();
    }

    public void updateResult() {
        ItemStack inputStaff = getStack(0);
        ItemStack patternItem = getStack(1);
        ItemStack itemForPattern = getStack(2);
        StaffMetals patternMetal = StaffMetals.getFromStack(itemForPattern);

        if (selectedPart == null || patternMetal == null || !inputStaff.isOf(ToolItems.STAFF) || !(patternItem.getItem() instanceof PatternTabletItem pattern)) {
            setStack(3, ItemStack.EMPTY);
            return;
        }

        if (!selectedPart.isStyled()) {
            Etherology.ELOGGER.error("Selected part has incompatible first StaffPattern");
            setStack(3, ItemStack.EMPTY);
            return;
        }

        StaffStyles patternStyle = pattern.getStaffStyle();
        ItemStack result = inputStaff.copy();
        StaffItem.setPartInfo(result, selectedPart, patternStyle, patternMetal);
        setStack(3, result);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void onSpecialEvent(int eventId, ItemStack stack) {}
}
