package ru.feytox.etherology.magic.lens;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;

public record LensModifier(Identifier modifierId) {

    public static final LensModifier PRESSURE = new LensModifier(new EIdentifier("pressure"));

    // constants
    public static final float PRESSURE_MODIFIER = 0.1f;
}
