package ru.feytox.etherology.magic.lens;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum LensMode implements StringIdentifiable {
    STREAM,
    CHARGE;

    public static final Codec<LensMode> CODEC = StringIdentifiable.createBasicCodec(LensMode::values);

    @Override
    public String asString() {
        return name();
    }
}
