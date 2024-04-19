package ru.feytox.etherology.block.armillary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringIdentifiable;

@Getter
@RequiredArgsConstructor
public enum ArmillaryState implements StringIdentifiable {
    IDLE,
    RESETTING,
    TESTED,
    PREPARED,
    CONSUMING,
    DECRYPTING_START,
    DECRYPTING,
    RESULTING;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
