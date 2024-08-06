package ru.feytox.etherology.gui.teldecore.page;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.util.misc.EIdentifier;

public class TitlePage extends AbstractPage {

    private static final Identifier TITLE_1 = EIdentifier.of("textures/gui/teldecore/page/title_1.png");
    private static final Identifier TITLE_2 = EIdentifier.of("textures/gui/teldecore/page/title_2.png");

    private final OrderedText title;

    public TitlePage(TeldecoreScreen parent, Text title, boolean isLeft, boolean isFancy) {
        super(parent, isFancy ? TITLE_1 : TITLE_2, isLeft, 33, 186);
        this.title = title.asOrderedText();
    }

    @Override
    public void renderPage(DrawContext context, float pageX, float pageY, int mouseX, int mouseY, float delta) {
        float x = pageX + (PAGE_WIDTH - textRenderer.getWidth(title)) / 2f;
        float y = pageY + 8;
        textRenderer.draw(title, x, y, 0x70523D, false, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
    }
}
