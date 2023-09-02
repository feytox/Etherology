package ru.feytox.etherology.magic.staff;

import java.util.List;
import java.util.function.Supplier;

public enum StaffStyles implements StaffPattern {
    ARISTOCRAT,
    ASTRONOMY,
    HEAVENLY,
    OCULAR,
    RITUAL,
    ROYAL,
    TRADITIONAL;

    public static final Supplier<List<? extends StaffPattern>> STYLES = StaffPattern.memoize(values());

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
