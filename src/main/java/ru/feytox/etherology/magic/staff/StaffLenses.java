package ru.feytox.etherology.magic.staff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.registry.item.EItems;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffLenses implements StaffPattern {
    REDSTONE(EItems.REDSTONE_LENS);

    public static final Supplier<List<? extends StaffPattern>> LENSES = StaffPattern.memoize(values());

    @Getter
    private final Item lensItem;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Nullable
    public static StaffLenses getLens(ItemStack lensStack) {
        Item lensItem = lensStack.getItem();
        if (!(lensItem instanceof LensItem)) return null;

        return Arrays.stream(values()).filter(lensType -> lensType.getLensItem().equals(lensItem)).toList().get(0);
    }
}
