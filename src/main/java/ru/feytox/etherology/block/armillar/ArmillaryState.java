package ru.feytox.etherology.block.armillar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringIdentifiable;

@Getter
@RequiredArgsConstructor
public enum ArmillaryState implements StringIdentifiable {
    IDLE(false),
    RESETTING(true),
    TESTED(true),
    PREPARED(true),
    CONSUMING(true),
    DECRYPTING(true),
    RESULTING(true); // TODO: 03.03.2024 rename

    private final boolean working;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
