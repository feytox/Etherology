package ru.feytox.etherology.item;

import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.lens.EtherLens;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.staff.StaffLenses;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.magic.staff.StaffPattern;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.function.Supplier;

/**
 * @see ru.feytox.etherology.magic.lens.LensComponent
 */
public abstract class LensItem extends Item implements EtherLens {

    public LensItem() {
        super(new FabricItemSettings().maxCount(1).maxDamage(100));
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    public abstract boolean onStreamUse(World world, LivingEntity entity, LensComponent lensData, boolean hold, Supplier<Hand> handGetter);
    public abstract boolean onChargeUse(World world, LivingEntity entity, LensComponent lensData, boolean hold, Supplier<Hand> handGetter);
    public void onStreamStop(World world, LivingEntity entity, LensComponent lensData, int holdTicks, Supplier<Hand> handGetter) {}
    public void onChargeStop(World world, LivingEntity entity, LensComponent lensData, int holdTicks, Supplier<Hand> handGetter) {}

    /**
     * Places a lens on a staff.
     *
     * @param staffStack the ItemStack representing the staff
     * @param lensStack the ItemStack representing the lens
     */
    public static void placeLensOnStaff(ItemStack staffStack, ItemStack lensStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return;
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return;
        if (!lensItem.isAdjusted()) return;

        val lens = EtherologyComponents.LENS.get(lensStack);
        val staff = EtherologyComponents.STAFF.get(staffStack);
        val staffLens = EtherologyComponents.LENS.get(staffStack);

        staffLens.copyFrom(lens);

        StaffLenses lensType = StaffLenses.getLens(lensStack);
        if (lensType == null) return;

        staff.setPartInfo(StaffPart.LENS, lensType, StaffPattern.EMPTY);
        lensStack.decrement(1);
    }

    /**
     * Takes a lens from the staff.
     *
     * @param staffStack the staff item stack
     * @param removeFromStack whether to remove the lens from staff
     * @return the lens item stack, or null if the staff does not have a lens
     */
    @Nullable
    public static ItemStack takeLensFromStaff(ItemStack staffStack, boolean removeFromStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return null;

        val staff = EtherologyComponents.STAFF.get(staffStack);
        val staffLens = EtherologyComponents.LENS.get(staffStack);

        StaffPartInfo lensInfo = staff.getPartInfo(StaffPart.LENS);
        if (lensInfo == null || !(lensInfo.getFirstPattern() instanceof StaffLenses lensType)) return null;

        if (removeFromStack) staff.removePartInfo(StaffPart.LENS);

        ItemStack lensStack = lensType.getLensItem().getDefaultStack();
        val lens = EtherologyComponents.LENS.get(lensStack);
        lens.copyFrom(staffLens);
        return lensStack;
    }

    public boolean isAdjusted() {
        return true;
    }
}
