package ru.feytox.etherology.magic.staff;

import java.util.List;
import java.util.function.Supplier;

public enum StaffColors implements StaffPattern {
    WHITE,
    BLACK,
    RED,
    PURPLE;

    public static final Supplier<List<? extends StaffPattern>> COLORS = StaffPattern.memoize(values());

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
