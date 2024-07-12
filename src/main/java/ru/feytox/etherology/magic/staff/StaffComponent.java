package ru.feytox.etherology.magic.staff;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.With;
import net.minecraft.item.ItemStack;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.ItemData;

import java.util.Map;
import java.util.Optional;

@With
public record StaffComponent(Map<StaffPart, StaffPartInfo> parts) {

    public static final Codec<StaffComponent> CODEC;
    public static final StaffComponent DEFAULT;

    public StaffComponent setPartInfo(StaffPartInfo partInfo) {
        Map<StaffPart, StaffPartInfo> newParts = new Object2ObjectOpenHashMap<>(parts);
        newParts.put(partInfo.part(), partInfo);
        return withParts(newParts);
    }

    public StaffComponent removePartInfo(StaffPart part) {
        Map<StaffPart, StaffPartInfo> newParts = new Object2ObjectOpenHashMap<>(parts);
        newParts.remove(part);
        return withParts(newParts);
    }

    public static Optional<ItemData<StaffComponent>> getWrapper(ItemStack stack) {
        return get(stack).map(component -> new ItemData<>(stack, ComponentTypes.STAFF, component));
    }

    public static Optional<StaffComponent> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(ComponentTypes.STAFF));
    }

    static {
        CODEC = Codec.unboundedMap(StaffPart.CODEC, StaffPartInfo.CODEC).xmap(StaffComponent::new, StaffComponent::parts).stable();
        DEFAULT = new StaffComponent(ImmutableMap.of(
                StaffPart.CORE, new StaffPartInfo(StaffPart.CORE, StaffMaterial.OAK, StaffPattern.EMPTY),
                StaffPart.HEAD, new StaffPartInfo(StaffPart.HEAD, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                StaffPart.DECOR, new StaffPartInfo(StaffPart.DECOR, StaffStyles.TRADITIONAL, StaffMetals.IRON),
                StaffPart.TIP, new StaffPartInfo(StaffPart.TIP, StaffStyles.TRADITIONAL, StaffMetals.IRON)
        ));
    }
}
