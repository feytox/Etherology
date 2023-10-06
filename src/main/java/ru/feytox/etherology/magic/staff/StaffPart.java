package ru.feytox.etherology.magic.staff;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffPart {
    CORE(false, StaffMaterial.MATERIALS),
    HANDLE(false, StaffColors.COLORS),
    DECOR(true, StaffStyles.STYLES, StaffMetals.METALS),
    HEAD(true, StaffStyles.STYLES, StaffMetals.METALS),
    LENSE(false, StaffColors.COLORS),
    TIP(true, StaffStyles.STYLES, StaffMetals.METALS);

    @Getter
    private final boolean styled;

    @Getter
    @NonNull
    private final Supplier<List<? extends StaffPattern>> firstPatterns;

    @Getter
    @NonNull
    private final Supplier<List<? extends StaffPattern>> secondPatterns;

    StaffPart(boolean styled, @NonNull Supplier<List<? extends StaffPattern>> firstPatterns) {
        this(styled, firstPatterns, StaffPattern.memoize(StaffPattern.EMPTY));
    }

    public String getName() {
        return name().toLowerCase();
    }
}
