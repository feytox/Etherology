package ru.feytox.etherology.util.misc;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class ScaledLabelComponent extends LabelComponent {
    private final float scale;

    public ScaledLabelComponent(Text text, float scale) {
        super(text);
        this.scale = scale;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        return Math.round(super.determineHorizontalContentSize(sizing) * scale);
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return Math.round(super.determineVerticalContentSize(sizing) * scale);
    }

    /**
     * @see LabelComponent#draw(OwoUIDrawContext, int, int, float, float)
     */
    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(0, 1 / MinecraftClient.getInstance().getWindow().getScaleFactor(), 0);

        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - (this.textHeight())) / 2;
            case BOTTOM -> y += this.height - (this.textHeight());
        }

        final int lambdaX = x;
        final int lambdaY = y;

        context.draw(() -> {
            for (int i = 0; i < this.wrappedText.size(); i++) {
                var renderText = this.wrappedText.get(i);
                int renderX = lambdaX;

                switch (this.horizontalTextAlignment) {
                    case CENTER -> renderX += (this.width - this.textRenderer.getWidth(renderText)) / 2;
                    case RIGHT -> renderX += this.width - this.textRenderer.getWidth(renderText);
                }

                int renderY = lambdaY + i * (this.lineHeight() + this.lineSpacing());
                renderY += this.lineHeight() - this.textRenderer.fontHeight;

                matrices.push();
                matrices.scale(scale, scale, 1);
                drawText(context, renderText, renderX / scale, renderY/ scale);
                matrices.pop();
            }
        });

        matrices.pop();
    }

    private void drawText(DrawContext context, OrderedText orderedText, float x, float y) {
        textRenderer.draw(orderedText, x, y, color.get().argb(), shadow, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        context.draw();
    }
}
