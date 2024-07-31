package ru.feytox.etherology.gui.teldecore.page;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;

public abstract class AbstractPage extends ParentedWidget {

    public static final int PAGE_WIDTH = 143;
    public static final int PAGE_HEIGHT = 196;

    private final Identifier pageTexture;
    @Getter
    private final boolean isLeft;
    private final float pageX;
    private final float pageY;
    private final float contentEnd;
    @Getter
    private float contentHeight;
    private final List<ParentedWidget> contentWidgets = new ObjectArrayList<>();

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

    /**
     * @param dx offset relative to the left page (the right page is automatically offset)
     * @return true if the content was added. false if the content doesn't fit into the available height.
     */
    public boolean addContent(AbstractContent content, float dx) {
        float height = content.getHeight(textRenderer) + content.getOffsetUp() + content.getOffsetDown();
        if (height > contentHeight) return false;

        contentWidgets.add(content.toWidget(parent, pageX+dx+(isLeft ? 0 : 4), pageY + (contentEnd - contentHeight + content.getOffsetUp())));
        contentHeight -= height;
        return true;
    }

    public void initContent() {
        contentWidgets.forEach(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, pageTexture);
        RenderUtils.renderTexture(context, pageX, pageY, isLeft ? 0 : PAGE_WIDTH, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH*2, PAGE_HEIGHT);
        renderPage(context, pageX, pageY, mouseX, mouseY, delta);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        // TODO: 30.07.2024 consider adding narrations
    }
}
