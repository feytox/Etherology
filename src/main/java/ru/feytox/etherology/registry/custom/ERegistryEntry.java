package ru.feytox.etherology.registry.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;

@RequiredArgsConstructor
public class ERegistryEntry<T> {
    @Getter
    private final Identifier identifier;

    @Getter
    private final T value;
}
