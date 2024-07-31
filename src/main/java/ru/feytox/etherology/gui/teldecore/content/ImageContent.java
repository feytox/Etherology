package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.util.misc.RenderUtils;

public class ImageContent extends AbstractContent {

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

    @Deprecated
    public static ImageContent of(Identifier texture, int textureWidth, int textureHeight) {
        return new ImageContent(texture, textureWidth, textureHeight, 0, 8);
    }

    @Override
    public float getHeight(TextRenderer textRenderer) {
        return height;
    }

    @Override
    public ParentedWidget toWidget(TeldecoreScreen parent, float x, float y) {
        return new Widget(parent, this, x, y);
    }

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
}
