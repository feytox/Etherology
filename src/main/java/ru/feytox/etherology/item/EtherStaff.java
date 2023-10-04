package ru.feytox.etherology.item;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import ru.feytox.etherology.magic.staff.*;

public class EtherStaff extends Item {

    public static final ImmutableList<StaffPartInfo> DEFAULT_STAFF;

    public EtherStaff() {
        super(new FabricItemSettings().maxCount(1));
    }

    public static void writeDefaultParts(NbtCompound stackNbt) {
        NbtList nbtList = new NbtList();

        DEFAULT_STAFF.stream()
                .map(partInfo -> {
                    NbtCompound nbt = new NbtCompound();
                    nbt.put(StaffPartInfo.NBT_KEY, partInfo);
                    return nbt;
                }).forEach(nbtList::add);

        stackNbt.put(StaffPartInfo.LIST_KEY, nbtList);
    }

    static {
        DEFAULT_STAFF = ImmutableList.of(
                new StaffPartInfo(StaffParts.CORE, StaffMaterial.OAK, StaffPattern.EMPTY),
                new StaffPartInfo(StaffParts.HEAD, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffParts.DECOR, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffParts.TIP, StaffStyles.TRADITIONAL, StaffMetals.IRON)
        );
    }
}
