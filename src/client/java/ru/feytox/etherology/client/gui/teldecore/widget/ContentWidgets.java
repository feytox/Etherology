package ru.feytox.etherology.client.gui.teldecore.widget;

import net.minecraft.client.font.TextRenderer;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.gui.teldecore.content.ImageContent;
import ru.feytox.etherology.gui.teldecore.content.RecipeContent;
import ru.feytox.etherology.gui.teldecore.content.TextContent;

import static ru.feytox.etherology.client.gui.teldecore.widget.TextWidget.wrapText;

public class ContentWidgets {

    public static ParentedWidget getWidget(AbstractContent abstractContent, TeldecoreScreen parent, float x, float y) {
        return switch (abstractContent) {
            case TextContent content -> new TextWidget(parent, wrapText(content, parent.getTextRenderer()), x, y);
            case RecipeContent content -> RecipeWidget.toWidget(content, parent, x, y);
            case ImageContent content -> new ImageWidget(parent, content, x, y);
            default -> throw new IllegalStateException("Unexpected value: " + abstractContent);
        };
    }

    public static float getHeight(AbstractContent abstractContent, TextRenderer textRenderer) {
        return switch (abstractContent)
        {
            case TextContent content -> TextWidget.getHeight(content, textRenderer);
            case RecipeContent content -> RecipeWidget.getHeight(content, textRenderer);
            case ImageContent content -> content.getHeight();
            default -> throw new IllegalStateException("Unexpected value: " + abstractContent);
        };
    }
}
