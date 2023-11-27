package ru.feytox.etherology.item;

import lombok.NonNull;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.lense.EtherLense;
import ru.feytox.etherology.magic.staff.*;

public abstract class LenseItem extends Item implements EtherLense {

    public LenseItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!stack.hasNbt()) writeNbt(stack.getOrCreateNbt(), new LenseData(LenseData.LenseMode.UP));
    }

    /**
     * Places a lense on a staff.
     *
     * @param staffStack the ItemStack representing the staff
     * @param lenseStack the ItemStack representing the lense
     */
    public static void placeLenseOnStaff(ItemStack staffStack, ItemStack lenseStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return;
        if (!(lenseStack.getItem() instanceof LenseItem)) return;

        LenseData lenseData = readNbt(lenseStack.getNbt());
        if (lenseData == null) return;
        NbtCompound staffNbt = staffStack.getNbt();
        if (staffNbt == null) return;
        writeNbt(staffNbt, lenseData);

        StaffLenses lensType = StaffLenses.getLense(lenseStack);
        if (lensType == null) return;

        boolean result = StaffItem.setPartInfo(staffStack, StaffPart.LENSE, lensType, StaffPattern.EMPTY);
        if (!result) return;
        lenseStack.decrement(1);
    }

    /**
     * Takes a lense from the staff.
     *
     * @param  staffStack  the staff item stack
     * @return             the lens item stack, or null if the staff does not have a lens
     */
    @Nullable
    public static ItemStack takeLenseFromStaff(ItemStack staffStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return null;

        NbtCompound staffNbt = staffStack.getNbt();
        if (staffNbt == null) return null;
        LenseData lenseData = readNbt(staffNbt);
        if (lenseData == null) return null;

        StaffPartInfo lensePartInfo = StaffItem.getPartInfo(staffStack, StaffPart.LENSE);
        if (lensePartInfo == null || !(lensePartInfo.getFirstPattern() instanceof StaffLenses lensType)) return null;

        StaffItem.removePartInfo(staffStack, StaffPart.LENSE);

        ItemStack lensStack = lensType.getLensItem().getDefaultStack();
        writeNbt(lensStack.getOrCreateNbt(), lenseData);
        return lensStack;
    }

    public static void writeNbt(@NonNull NbtCompound nbt, @NonNull LenseData lenseData) {
        NbtCompound subNbt = new NbtCompound();
        subNbt.putString("mode", lenseData.getLenseMode().name());
        nbt.put("lense", subNbt);
    }

    @Nullable
    public static LenseData readNbt(@Nullable NbtCompound nbt) {
        if (nbt == null) return null;
        NbtCompound subNbt = nbt.getCompound("lense");
        if (subNbt.isEmpty()) return null;

        LenseData.LenseMode mode = EnumUtils.getEnumIgnoreCase(LenseData.LenseMode.class, subNbt.getString("mode"), null);
        if (mode == null) return null;
        return new LenseData(mode);
    }

    public static class EmptyLense extends LenseItem {
    }

    public static class TestLense extends LenseItem {
    }
}
