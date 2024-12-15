package ru.feytox.etherology.client.gui.teldecore.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.util.RenderUtils;
import ru.feytox.etherology.gui.teldecore.content.ImageContent;

public class ImageWidget extends ParentedWidget {

    private final ImageContent content;

    public ImageWidget(TeldecoreScreen parent, ImageContent content, float baseX, float baseY) {
        super(parent, baseX, baseY);
        this.content = content;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, content.getTexture());
        RenderUtils.renderTexture(context, baseX, baseY, 0, 0, ImageContent.MAX_WIDTH, content.getHeight(), content.getTextureWidth(), content.getTextureHeight());
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
