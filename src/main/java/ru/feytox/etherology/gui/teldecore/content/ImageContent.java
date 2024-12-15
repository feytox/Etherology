package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
public class ImageContent extends AbstractContent {

    public static final MapCodec<ImageContent> CODEC;
    // see AbstractPage constants
    public static final float MAX_WIDTH = 143 - 20;

    private final Identifier texture;
    private final int textureWidth;
    private final int textureHeight;
    private final float height;

    public ImageContent(Identifier texture, int textureWidth, int textureHeight, float offsetUp, float offsetDown, ContentBehaviour behaviour) {
        super(offsetUp, offsetDown, behaviour);
        this.texture = texture;
        this.height = textureHeight * (MAX_WIDTH / textureWidth);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public String getType() {
        return "image";
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("path").forGetter(c -> c.texture),
                Codec.INT.fieldOf("texture_width").forGetter(c -> c.textureWidth),
                Codec.INT.fieldOf("texture_height").forGetter(c -> c.textureHeight),
                codecOffsetUp(), codecOffsetDown(), codecBehaviour()).apply(instance, ImageContent::new));
    }
}
