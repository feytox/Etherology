package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.font.TextRenderer;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;

@RequiredArgsConstructor @Getter
public abstract class AbstractContent {

    private final float offsetUp;
    private final float offsetDown;
    private final ContentBehaviour behaviour;

    public abstract String getType();
    public abstract float getHeight(TextRenderer textRenderer);
    public abstract ParentedWidget toWidget(TeldecoreScreen parent, float x, float y);

    protected static <T extends AbstractContent> RecordCodecBuilder<T, Float> codecOffsetUp() {
        return Codec.FLOAT.optionalFieldOf("up", 0f).forGetter(AbstractContent::getOffsetUp);
    }

    protected static <T extends AbstractContent> RecordCodecBuilder<T, Float> codecOffsetDown() {
        return Codec.FLOAT.optionalFieldOf("down", 8f).forGetter(AbstractContent::getOffsetDown);
    }

    protected static <T extends AbstractContent> RecordCodecBuilder<T, ContentBehaviour> codecBehaviour() {
        return ContentBehaviour.CODEC.optionalFieldOf("show", ContentBehaviour.ALWAYS).forGetter(AbstractContent::getBehaviour);
    }
}
