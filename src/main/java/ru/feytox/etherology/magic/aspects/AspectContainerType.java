package ru.feytox.etherology.magic.aspects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum AspectContainerType {
    ITEM(null),
    ENTITY,
    POTION,
    SPLASH_POTION,
    LINGERING_POTION,
    TIPPED_ARROW;

    @Nullable
    private final String prefix;

    AspectContainerType() {
        this.prefix = name().toLowerCase();
    }

    public static AspectContainerType getByPrefix(String prefix, AspectContainerType defaultVal) {
        return EnumUtils.getEnumIgnoreCase(AspectContainerType.class, prefix, defaultVal);
    }
}
