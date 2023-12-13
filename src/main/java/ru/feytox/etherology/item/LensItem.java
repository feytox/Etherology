package ru.feytox.etherology.item;

import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.lense.EtherLense;
import ru.feytox.etherology.magic.lense.LensComponent;
import ru.feytox.etherology.magic.staff.StaffLenses;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.magic.staff.StaffPattern;
import ru.feytox.etherology.registry.util.EtherologyComponents;

public abstract class LensItem extends Item implements EtherLense {

    public LensItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    public abstract boolean onStreamUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold);
    public abstract boolean onChargeUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold);
    public void onStreamStop(World world, LivingEntity entity, LensComponent lenseData) {}
    public void onChargeStop(World world, LivingEntity entity, LensComponent lenseData) {}

    /**
     * Places a lens on a staff.
     *
     * @param staffStack the ItemStack representing the staff
     * @param lenseStack the ItemStack representing the lense
     */
    public static void placeLenseOnStaff(ItemStack staffStack, ItemStack lenseStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return;
        if (!(lenseStack.getItem() instanceof LensItem lensItem)) return;
        if (!lensItem.isAdjusted()) return;

        val lense = EtherologyComponents.LENS.get(lenseStack);
        val staff = EtherologyComponents.STAFF.get(staffStack);
        val staffLense = EtherologyComponents.LENS.get(staffStack);

        staffLense.copyFrom(lense);

        StaffLenses lensType = StaffLenses.getLens(lenseStack);
        if (lensType == null) return;

        staff.setPartInfo(StaffPart.LENS, lensType, StaffPattern.EMPTY);
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

        val staff = EtherologyComponents.STAFF.get(staffStack);
        val staffLense = EtherologyComponents.LENS.get(staffStack);

        StaffPartInfo lensInfo = staff.getPartInfo(StaffPart.LENS);
        if (lensInfo == null || !(lensInfo.getFirstPattern() instanceof StaffLenses lensType)) return null;

        staff.removePartInfo(StaffPart.LENS);

        ItemStack lensStack = lensType.getLensItem().getDefaultStack();
        val lense = EtherologyComponents.LENS.get(lensStack);
        lense.copyFrom(staffLense);
        return lensStack;
    }

    public boolean isAdjusted() {
        return true;
    }
}
