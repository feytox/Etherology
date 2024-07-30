package ru.feytox.etherology.gui.teldecore.content;

import lombok.NonNull;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.gui.teldecore.page.AbstractPage;

import java.util.List;

public class TextContent extends AbstractContent {

    public static final int TEXT_WIDTH = AbstractPage.PAGE_WIDTH - 12;
    private static final int TEXT_HEIGHT = 6;
    private static final int TEXT_SPACE = 4;

    @NonNull
    private final Text text;

    public TextContent(@NotNull Text text, float offsetUp, float offsetDown) {
        super(offsetUp, offsetDown);
        this.text = text;
    }

    public static TextContent of(Text text) {
        return new TextContent(text, 0, 8);
    }

    @Override
    public float getHeight(TextRenderer textRenderer) {
        List<OrderedText> rows = wrapText(textRenderer);
        int size = rows.size();
        return TEXT_HEIGHT * size + TEXT_SPACE * (size-1);
    }

    // TODO: 30.07.2024 consider caching rows
    private List<OrderedText> wrapText(TextRenderer textRenderer) {
        return textRenderer.wrapLines(text, TEXT_WIDTH);
    }

    @Override
    public ParentedWidget toWidget(TeldecoreScreen parent, float x, float y) {
        return new Widget(parent, wrapText(parent.getTextRenderer()), x, y);
    }

    public static class Widget extends ParentedWidget {

        private final List<OrderedText> textRows;

        public Widget(TeldecoreScreen parent, List<OrderedText> textRows, float baseX, float baseY) {
            super(parent, baseX, baseY);
            this.textRows = textRows;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            for (int i = 0; i < textRows.size(); i++) {
                textRenderer.draw(textRows.get(i), baseX, baseY + i*(TEXT_HEIGHT+TEXT_SPACE), 0x70523D, false, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            }
        }

        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}
    }
}
