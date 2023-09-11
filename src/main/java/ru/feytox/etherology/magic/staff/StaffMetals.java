package ru.feytox.etherology.magic.staff;

import java.util.List;
import java.util.function.Supplier;

public enum StaffMetals implements StaffPattern {
    ATTRAHITE,
    COPPER,
    ETHRIL,
    GOLD,
    IRON,
    NETHERITE,
    TELDER;

    public static final Supplier<List<? extends StaffPattern>> METALS = StaffPattern.memoize(values());

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}