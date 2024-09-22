package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor @Getter
public enum ChapterType implements StringIdentifiable {
    INFO(EIdentifier.of("textures/gui/teldecore/icon/chapter_hexagon.png"), EIdentifier.of("textures/gui/teldecore/icon/chapter_hexagon_hidden.png")),
    QUEST(EIdentifier.of("textures/gui/teldecore/icon/chapter_round.png"), EIdentifier.of("textures/gui/teldecore/icon/chapter_round_hidden.png")),
    SUB_TAB(EIdentifier.of("textures/gui/teldecore/icon/chapter_square.png"), EIdentifier.of("textures/gui/teldecore/icon/chapter_square_hidden.png")),
    NEW_TAB(EIdentifier.of("textures/gui/teldecore/icon/chapter_star.png"), EIdentifier.of("textures/gui/teldecore/icon/chapter_star_hidden.png"));

    public static final Codec<ChapterType> CODEC = StringIdentifiable.createBasicCodec(ChapterType::values);

    private final Identifier texture;
    private final Identifier hiddenTexture;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
