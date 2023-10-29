package ru.feytox.etherology.item;

import com.google.common.collect.ImmutableList;
import lombok.val;
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

    public static void setPartInfo(ItemStack stack, StaffPart part, StaffPattern firstPattern, StaffPattern secondPattern) {
        val staffData = readNbt(stack);
        if (staffData == null) {
            Etherology.ELOGGER.error("Null staff data after staff nbt reading");
            return;
        }

        StaffPartInfo partInfo = new StaffPartInfo(part, firstPattern, secondPattern);
        staffData.put(part, partInfo);
        writeNbt(stack, staffData);
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

    static {
        DEFAULT_STAFF = ImmutableList.of(
                new StaffPartInfo(StaffPart.CORE, StaffMaterial.OAK, StaffPattern.EMPTY),
                new StaffPartInfo(StaffPart.HEAD, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffPart.DECOR, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                new StaffPartInfo(StaffPart.TIP, StaffStyles.TRADITIONAL, StaffMetals.IRON)
        );
    }
}
