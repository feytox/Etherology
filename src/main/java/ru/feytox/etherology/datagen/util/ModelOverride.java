package ru.feytox.etherology.datagen.util;

import net.minecraft.util.Identifier;

public record ModelOverride(Identifier replaceModelId, String predicateName, float predicateValue) {
}
