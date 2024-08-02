package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor @Getter
public enum ChapterType implements StringIdentifiable {
    INFO(EIdentifier.of("textures/gui/teldecore/icon/chapter_0.png")),
    REQUIRED(EIdentifier.of("textures/gui/teldecore/icon/chapter_1.png")),
    IMPORTANT(EIdentifier.of("textures/gui/teldecore/icon/chapter_2.png"));

    public static final Codec<ChapterType> CODEC = StringIdentifiable.createBasicCodec(ChapterType::values);

    private final Identifier texture;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
