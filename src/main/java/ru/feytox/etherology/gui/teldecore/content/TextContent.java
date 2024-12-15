package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;


@Getter
public class TextContent extends AbstractContent {

    public static final MapCodec<TextContent> CODEC;

    @NonNull
    private final String textKey;

    public TextContent(@NotNull String textKey, float offsetUp, float offsetDown, ContentBehaviour behaviour) {
        super(offsetUp, offsetDown, behaviour);
        this.textKey = textKey;
    }

    @Override
    public String getType() {
        return "text";
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("text").forGetter(c -> c.textKey),
                codecOffsetUp(), codecOffsetDown(), codecBehaviour()).apply(instance, TextContent::new));
    }
}
