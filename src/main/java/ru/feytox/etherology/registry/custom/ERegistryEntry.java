package ru.feytox.etherology.registry.custom;

import net.minecraft.util.Identifier;

@Deprecated
public record ERegistryEntry<T>(Identifier identifier, T value) {
}
