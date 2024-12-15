package ru.feytox.etherology.client.gui.teldecore.page;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.gui.teldecore.widget.ContentWidgets;
import ru.feytox.etherology.client.gui.teldecore.widget.ParentedWidget;
import ru.feytox.etherology.client.util.RenderUtils;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;

import java.util.List;

public abstract class AbstractPage extends ParentedWidget {

    public static final int PAGE_WIDTH = 143;
    public static final int PAGE_HEIGHT = 196;

    private final Identifier pageTexture;
    @Getter
    private final boolean isLeft;
    @Getter
    private final float pageX;
    @Getter
    private final float pageY;
    private final float contentEnd;
    @Getter
    private float contentHeight;
    private final List<ParentedWidget> widgets = new ObjectArrayList<>();
    @Setter
    private int pageIndex = -1;

    public AbstractPage(TeldecoreScreen parent, Identifier pageTexture, boolean isLeft, float contentStart, float contentEnd) {
        super(parent);
        this.pageTexture = pageTexture;
        this.isLeft = isLeft;
        this.contentHeight = contentEnd - contentStart;
        this.contentEnd = contentEnd;
        this.pageX = baseX + 12 + (isLeft ? 0 : PAGE_WIDTH);
        this.pageY = baseY + 7;
    }

    public abstract void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta);

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= baseX && mouseY >= baseY && mouseX <= baseX+PAGE_WIDTH && mouseY <= baseY+PAGE_HEIGHT;
    }

    /**
     * @param dx offset relative to the left page (the right page is automatically offset)
     * @return true if the content was added. false if the content doesn't fit into the available height.
     */
    public boolean addContent(AbstractContent content, float dx) {
        var height = ContentWidgets.getHeight(content, textRenderer) + content.getOffsetUp() + content.getOffsetDown();
        if (height > contentHeight) return false;

        var contentWidget = ContentWidgets.getWidget(content, parent, pageX+dx, pageY + (contentEnd - contentHeight + content.getOffsetUp()));
        widgets.add(contentWidget);
        contentHeight -= height;
        return true;
    }

    public void initContent() {
        widgets.forEach(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, pageTexture);
        RenderUtils.renderTexture(context, pageX, pageY, isLeft ? 0 : PAGE_WIDTH, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH*2, PAGE_HEIGHT);
        renderPage(context, pageX, pageY, mouseX, mouseY, delta);
        renderPageIndex(context);
    }

    public void renderPageIndex(DrawContext context) {
        if (this.pageIndex == -1) return;
        var pageIndex = Text.literal(String.valueOf(this.pageIndex)).asOrderedText();
        float x = pageX + (PAGE_WIDTH - textRenderer.getWidth(pageIndex)) / 2f;
        var y = pageY + PAGE_HEIGHT - 10;
        TeldecoreScreen.renderText(context, textRenderer, pageIndex, x, y);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        // TODO: 30.07.2024 add narrations (???)
    }
}
