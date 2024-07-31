package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.util.misc.RenderUtils;

public class ImageContent extends AbstractContent {

    public static final MapCodec<ImageContent> CODEC;
    public static final float MAX_WIDTH = AbstractPage.PAGE_WIDTH - 12;

    private final Identifier texture;
    private final int textureWidth;
    private final int textureHeight;
    private final float height;

    public ImageContent(Identifier texture, int textureWidth, int textureHeight, float offsetUp, float offsetDown) {
        super(offsetUp, offsetDown);
        this.texture = texture;
        this.height = textureHeight * (MAX_WIDTH / textureWidth);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public float getHeight(TextRenderer textRenderer) {
        return height;
    }

    @Override
    public ParentedWidget toWidget(TeldecoreScreen parent, float x, float y) {
        return new Widget(parent, this, x, y);
    }

    @Override
    public String getType() {
        return "image";
    }

    @Environment(EnvType.CLIENT)
    private static class Widget extends ParentedWidget {

        private final ImageContent content;

        public Widget(TeldecoreScreen parent, ImageContent content, float baseX, float baseY) {
            super(parent, baseX, baseY);
            this.content = content;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, content.texture);
            RenderUtils.renderTexture(context, baseX, baseY, 0, 0, MAX_WIDTH, content.height, content.textureWidth, content.textureHeight);
        }

        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("path").forGetter(c -> c.texture),
                Codec.INT.fieldOf("texture_width").forGetter(c -> c.textureWidth),
                Codec.INT.fieldOf("texture_height").forGetter(c -> c.textureHeight),
                codecOffsetUp(), codecOffsetDown()).apply(instance, ImageContent::new));
    }
}
