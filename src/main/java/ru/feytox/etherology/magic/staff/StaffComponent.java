package ru.feytox.etherology.magic.staff;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StaffComponent extends ItemComponent {

    private static final Supplier<ImmutableMap<StaffPart, StaffPartInfo>> DEFAULT_PARTS;

    @Nullable
    private Map<StaffPart, StaffPartInfo> cached;

    public StaffComponent(ItemStack stack) {
        super(stack);
    }

    public void setPartInfo(StaffPart part, StaffPattern firstPattern, StaffPattern secondPattern) {
        val parts = getParts();
        StaffPartInfo partInfo = new StaffPartInfo(part, firstPattern, secondPattern);
        parts.put(part, partInfo);
        putParts(parts);
    }

    @Nullable
    public StaffPartInfo getPartInfo(StaffPart part) {
        val parts = getParts();
        return parts.getOrDefault(part, null);
    }

    public void removePartInfo(StaffPart part) {
        val parts = getParts();
        parts.remove(part);
        putParts(parts);
    }

    public Map<StaffPart, StaffPartInfo> getParts() {
        if (cached != null) return cached;
        if (!hasTag("parts")) putParts(DEFAULT_PARTS.get());

        NbtCompound partsNbt = getCompound("parts");
        cached = partsNbt.getKeys().stream()
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
        return cached;
    }

    private void putParts(Map<StaffPart, StaffPartInfo> parts) {
        NbtCompound partsNbt = new NbtCompound();

        parts.forEach((part, partInfo) -> {
            NbtCompound nbt = new NbtCompound();
            nbt.put(StaffPartInfo.NBT_KEY, partInfo);
            partsNbt.put(partInfo.getPart().getName(), nbt);
        });

        putCompound("parts", partsNbt);
        resetCache();
    }

    @Override
    public void onTagInvalidated() {
        super.onTagInvalidated();
        resetCache();
    }

    public void resetCache() {
        cached = null;
    }

    static {
        DEFAULT_PARTS = Suppliers.memoize(() -> ImmutableMap.of(
                StaffPart.CORE, new StaffPartInfo(StaffPart.CORE, StaffMaterial.OAK, StaffPattern.EMPTY),
                StaffPart.HEAD, new StaffPartInfo(StaffPart.HEAD, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                StaffPart.DECOR, new StaffPartInfo(StaffPart.DECOR, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                StaffPart.TIP, new StaffPartInfo(StaffPart.TIP, StaffStyles.TRADITIONAL, StaffMetals.IRON)
        ));
    }
}
