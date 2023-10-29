package ru.feytox.etherology.magic.staff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.registry.item.DecoBlockItems;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffMetals implements StaffPattern {
    ATTRAHITE(DecoBlockItems.ATTRAHITE_INGOT),
    COPPER(Items.COPPER_INGOT),
    ETHRIL(DecoBlockItems.ETHRIL_INGOT),
    GOLD(Items.GOLD_INGOT),
    IRON(Items.IRON_INGOT),
    NETHERITE(Items.NETHERITE_INGOT),
    TELDER(DecoBlockItems.TELDER_STEEL_INGOT);

    public static final Supplier<List<? extends StaffPattern>> METALS = StaffPattern.memoize(values());

    @Getter
    private final Item metalItem;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Nullable
    public static StaffMetals getFromStack(ItemStack metalStack) {
        if (metalStack.isEmpty()) return null;

        Item metalItem = metalStack.getItem();
        String itemId = Registries.ITEM.getId(metalItem).getPath();
        String metalId = itemId.split("_")[0];
        StaffMetals result = EnumUtils.getEnumIgnoreCase(StaffMetals.class, metalId, null);
        if (result != null) return result;

        // TODO: 29.10.2023 проверить необходимость ИЛИ просто указать требование для названий StaffMetals
        for (StaffMetals staffMetal : values()) {
            if (metalItem.equals(staffMetal.metalItem)) return staffMetal;
        }

        return null;
    }
}
