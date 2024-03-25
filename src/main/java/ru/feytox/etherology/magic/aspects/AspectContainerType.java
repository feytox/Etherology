package ru.feytox.etherology.magic.aspects;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Getter(lazy = true)
    private static final Map<String, AspectContainerType> prefixToType = Arrays.stream(values()).filter(type -> !type.equals(ITEM)).collect(Collectors.toMap(AspectContainerType::getPrefix, type -> type, (type1, type2) -> type1, Object2ObjectOpenHashMap::new));
}
