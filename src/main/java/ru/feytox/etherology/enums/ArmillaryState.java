package ru.feytox.etherology.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringIdentifiable;

@RequiredArgsConstructor
public enum ArmillaryState implements StringIdentifiable {
    OFF(false),
    RAISING(false),
    STORING(true),
    CRAFTING(true),
    CONSUMING(true),
    DAMAGING(true),
    LOWERING(false);

    @Getter
    private final boolean working;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
