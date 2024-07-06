package ru.feytox.etherology.util.misc;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.pond.OwoTextRendererExtension;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
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
     * @see LabelComponent#draw(MatrixStack, int, int, float, float)
     */
    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        try {
            int x = this.x;
            int y = this.y;

            if (this.horizontalSizing.get().isContent()) {
                x += this.horizontalSizing.get().value;
            }
            if (this.verticalSizing.get().isContent()) {
                y += this.verticalSizing.get().value;
            }

            switch (this.verticalTextAlignment) {
                case CENTER -> y += (this.height - ((this.wrappedText.size() * (this.textRenderer.fontHeight + 2)) - 2)) / 2;
                case BOTTOM -> y += this.height - ((this.wrappedText.size() * (this.textRenderer.fontHeight + 2)) - 2);
            }

            ((OwoTextRendererExtension) this.textRenderer).owo$beginCache();

            for (int i = 0; i < this.wrappedText.size(); i++) {
                var renderText = this.wrappedText.get(i);
                int renderX = x;

                switch (this.horizontalTextAlignment) {
                    case CENTER -> renderX += (this.width - this.textRenderer.getWidth(renderText)) / 2;
                    case RIGHT -> renderX += this.width - this.textRenderer.getWidth(renderText);
                }

                matrices.push();
                matrices.scale(scale, scale, 1);
                if (this.shadow) {
                    this.textRenderer.drawWithShadow(matrices, renderText, renderX / scale, (y + i * 11) / scale, this.color.get().argb());
                } else {
                    this.textRenderer.draw(matrices, renderText, renderX / scale, (y + i * 11) / scale, this.color.get().argb());
                }
                matrices.pop();
            }
        } finally {
            ((OwoTextRendererExtension) this.textRenderer).owo$submitCache();
        }
    }
}
