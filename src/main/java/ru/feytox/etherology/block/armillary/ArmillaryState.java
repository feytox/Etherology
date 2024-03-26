package ru.feytox.etherology.block.armillary;

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
    RESULTING(true);

    private final boolean working;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
