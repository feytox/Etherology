package ru.feytox.etherology.magic.staff;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum StaffParts {
    CORE("core", StaffMaterial.MATERIALS),
//    HANDLE("handle", StaffColors.COLORS),
    DECOR("style", StaffStyles.STYLES, StaffMetals.METALS),
    HEAD("style", StaffStyles.STYLES, StaffMetals.METALS),
    LENSE("lense", StaffColors.COLORS),
    TIP("style", StaffStyles.STYLES, StaffMetals.METALS);

    @Getter
    @NonNull
    private final String textureSuffix;

    @Getter
    @NonNull
    private final Supplier<List<? extends StaffPattern>> firstPatterns;

    @Getter
    @NonNull
    private final Supplier<List<? extends StaffPattern>> secondPatterns;

    StaffParts(String textureSuffix, @NonNull Supplier<List<? extends StaffPattern>> firstPatterns) {
        this(textureSuffix, firstPatterns, StaffPattern.memoize(StaffPattern.EMPTY));
    }

    public String getName() {
        return name().toLowerCase();
    }
}
