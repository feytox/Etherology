package ru.feytox.etherology.magic.staff;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffColors implements StaffPattern {
    BLACK(Items.BLACK_CARPET),
    BLUE(Items.BLUE_CARPET),
    BROWN(Items.BROWN_CARPET),
    CYAN(Items.CYAN_CARPET),
    GRAY(Items.GRAY_CARPET),
    GREEN(Items.GREEN_CARPET),
    LIGHT_BLUE(Items.LIGHT_BLUE_CARPET),
    LIGHT_GRAY(Items.LIGHT_GRAY_CARPET),
    LIME(Items.LIME_CARPET),
    MAGENTA(Items.MAGENTA_CARPET),
    ORANGE(Items.ORANGE_CARPET),
    PINK(Items.PINK_CARPET),
    PURPLE(Items.PURPLE_CARPET),
    RED(Items.RED_CARPET),
    WHITE(Items.WHITE_CARPET),
    YELLOW(Items.YELLOW_CARPET);

    public static final Supplier<List<? extends StaffPattern>> COLORS = StaffPattern.memoize(values());

    @Getter
    private final Item carpet;

    @Nullable
    public static StaffColors getFromColorName(String colorName) {
        return EnumUtils.getEnumIgnoreCase(StaffColors.class, colorName, null);
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
