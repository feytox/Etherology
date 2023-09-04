package ru.feytox.etherology.magic.staff;

import java.util.List;
import java.util.function.Supplier;

public enum StaffMaterial implements StaffPattern {
    OAK;

    public static final Supplier<List<? extends StaffPattern>> MATERIALS = StaffPattern.memoize(values());

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
