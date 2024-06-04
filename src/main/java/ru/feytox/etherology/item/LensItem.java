package ru.feytox.etherology.item;

import lombok.Getter;
import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.lens.EtherLens;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.staff.StaffLenses;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPattern;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.function.Supplier;

/**
 * @see ru.feytox.etherology.magic.lens.LensComponent
 */
@Getter
public abstract class LensItem extends Item implements EtherLens {

    @Nullable
    private final StaffLenses lensType;

    protected LensItem(@Nullable StaffLenses lensType) {
        super(new FabricItemSettings().maxCount(1).maxDamage(100));
        this.lensType = lensType;
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
     * Places a lens on a staff. Before this you need to take old lens from staff
     *
     * @param staffStack the ItemStack representing the staff
     * @param lensStack the ItemStack representing the lens
     */
    public static void placeLensOnStaff(ItemStack staffStack, ItemStack lensStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return;
        if (!(lensStack.getItem() instanceof LensItem lensItem)) return;
        if (lensItem.isUnadjusted()) return;

        val staff = EtherologyComponents.STAFF.get(staffStack);

        StaffLenses lensType = StaffLenses.getLens(lensStack);
        if (lensType == null) return;

        NbtCompound lensNbt = new NbtCompound();
        lensStack.writeNbt(lensNbt);
        staffStack.setSubNbt("lens_data", lensNbt);

        staff.setPartInfo(StaffPart.LENS, lensType, StaffPattern.EMPTY);
        lensStack.decrement(1);
    }

    /**
     * Takes a lens from the staff.
     *
     * @param staffStack the staff item stack
     * @return the lens item stack, or null if the staff does not have a lens
     */
    @Nullable
    public static ItemStack takeLensFromStaff(ItemStack staffStack) {
        if (!(staffStack.getItem() instanceof StaffItem)) return null;

        ItemStack lensStack = getLensStack(staffStack);
        if (lensStack == null) return null;

        val staff = EtherologyComponents.STAFF.get(staffStack);
        staff.removePartInfo(StaffPart.LENS);
        staffStack.removeSubNbt("lens_data");

        return lensStack;
    }

    @Nullable
    public static LensComponent getStaffLens(ItemStack staffStack) {
        ItemStack lensStack = getLensStack(staffStack);
        return lensStack == null ? null : EtherologyComponents.LENS.getNullable(lensStack);
    }

    @Nullable
    public static ItemStack getLensStack(ItemStack staffStack) {
        NbtCompound lensNbt = staffStack.getSubNbt("lens_data");
        if (lensNbt == null) return null;
        ItemStack lensStack = ItemStack.fromNbt(lensNbt);
        return lensStack.isEmpty() ? null : lensStack;
    }

    public boolean isUnadjusted() {
        return false;
    }
}
