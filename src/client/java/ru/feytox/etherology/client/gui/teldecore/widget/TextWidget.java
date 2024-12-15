package ru.feytox.etherology.client.gui.teldecore.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.gui.teldecore.page.AbstractPage;
import ru.feytox.etherology.gui.teldecore.content.TextContent;

import java.util.List;

public class TextWidget extends ParentedWidget {

    private static final int TEXT_WIDTH = AbstractPage.PAGE_WIDTH - 20;
    private static final int TEXT_HEIGHT = 6;
    private static final int TEXT_SPACE = 4;
    private final List<OrderedText> textRows;

    public TextWidget(TeldecoreScreen parent, List<OrderedText> textRows, float baseX, float baseY) {
        super(parent, baseX, baseY);
        this.textRows = textRows;
    }

    public static float getHeight(TextContent content, TextRenderer textRenderer) {
        var rows = wrapText(content, textRenderer);
        var size = rows.size();
        return TEXT_HEIGHT * size + TEXT_SPACE * (size - 1);
    }

    // TODO: 30.07.2024 cache rows
    public static List<OrderedText> wrapText(TextContent content, TextRenderer textRenderer) {
        return textRenderer.wrapLines(Text.translatable(content.getTextKey()), TEXT_WIDTH);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < textRows.size(); i++) {
            TeldecoreScreen.renderText(context, textRenderer, textRows.get(i), baseX, baseY + i * (TEXT_HEIGHT + TEXT_SPACE));
        }
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
