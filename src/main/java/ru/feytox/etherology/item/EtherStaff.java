package ru.feytox.etherology.item;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.magic.staff.*;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EtherStaff extends Item {

    public static final ImmutableList<StaffPartInfo> DEFAULT_STAFF;

    public EtherStaff() {
        super(new FabricItemSettings().maxCount(1));
    }

    public static void writeDefaultParts(NbtCompound stackNbt) {
        NbtCompound parts = new NbtCompound();

        DEFAULT_STAFF.forEach(partInfo -> {
                    NbtCompound nbt = new NbtCompound();
                    nbt.put(StaffPartInfo.NBT_KEY, partInfo);
                    parts.put(partInfo.getPart().getName(), nbt);
                });

        stackNbt.put("parts", parts);
    }

    public static void writeNbt(ItemStack stack, Map<StaffPart, StaffPartInfo> parts) {
        NbtCompound stackNbt = stack.getOrCreateNbt();
        NbtCompound partsNbt = new NbtCompound();

        parts.forEach((part, partInfo) -> {
            NbtCompound nbt = new NbtCompound();
            nbt.put(StaffPartInfo.NBT_KEY, partInfo);
            partsNbt.put(partInfo.getPart().getName(), nbt);
        });

        stackNbt.put("parts", partsNbt);
        stack.setNbt(stackNbt);
    }

    @Nullable
    public static Map<StaffPart, StaffPartInfo> readNbt(ItemStack stack) {
        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null || stackNbt.isEmpty()) return null;

        NbtCompound partsNbt = stackNbt.getCompound("parts");
        if (partsNbt.isEmpty()) return null;
        return partsNbt.getKeys().stream()
                .map(partsNbt::getCompound)
                .map(nbt -> {
                    try {
                        return nbt.get(StaffPartInfo.NBT_KEY);
                    } catch (Exception e) {
                        Etherology.ELOGGER.error("Found non-PartInfo element while loading EtherStaff NBT");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(StaffPartInfo::getPart, part -> part));
    }

//    @Nullable
//    public static List<StaffPartInfo> readNbt(ItemStack stack) {
//        NbtCompound stackNbt = stack.getNbt();
//        if (stackNbt == null) return null;
//
//        NbtList nbtList = stackNbt.getOr(StaffPartInfo.LIST_KEY, new NbtList());
//        return nbtList.stream()
//                .map(nbtElement -> {
//                    if (nbtElement instanceof NbtCompound compound) return compound;
//                    Etherology.ELOGGER.error("Found a non-NbtCompound element while loading EtherStaff NBT");
//                    return null;
//                })
//                .filter(Objects::nonNull)
//                .map(nbt -> {
//                    try {
//                        return nbt.get(StaffPartInfo.NBT_KEY);
//                    } catch (Exception e) {
//                        Etherology.ELOGGER.error("Found non-PartInfo element while loading EtherStaff NBT");
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .collect(Collectors.toCollection(ObjectArrayList::new));
//    }

    static {
        DEFAULT_STAFF = ImmutableList.of(
                new StaffPartInfo(StaffPart.CORE, StaffMaterial.OAK, StaffPattern.EMPTY),
                new StaffPartInfo(StaffPart.HEAD, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffPart.DECOR, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffPart.TIP, StaffStyles.TRADITIONAL, StaffMetals.IRON)
        );
    }
}
