package ru.feytox.etherology.magic.staff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LenseItem;
import ru.feytox.etherology.registry.item.EItems;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffLenses implements StaffPattern {
    BLANK(EItems.EMPTY_LENSE),
    TEST(EItems.TEST_LENSE);

    public static final Supplier<List<? extends StaffPattern>> LENSES = StaffPattern.memoize(values());

    @Getter
    private final Item lensItem;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Nullable
    public static StaffLenses getLense(ItemStack lensStack) {
        Item lensItem = lensStack.getItem();
        if (!(lensItem instanceof LenseItem)) return null;

        return Arrays.stream(values()).filter(lensType -> lensType.getLensItem().equals(lensItem)).toList().get(0);
    }
}
