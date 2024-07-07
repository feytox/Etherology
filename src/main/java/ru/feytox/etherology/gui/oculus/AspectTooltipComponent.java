package ru.feytox.etherology.gui.oculus;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Map;

public class AspectTooltipComponent implements TooltipComponent {
    private static final Identifier TEXTURE = EIdentifier.of("textures/gui/aspects.png");
    private static final int LINE_MAX = 5;
    private final AspectContainer aspects;

    public AspectTooltipComponent(AspectContainer aspects) {
        this.aspects = aspects;
    }

    @Override
    public int getHeight() {
        int count = aspects.getAspects().size();
        return 18 * (1 + count / (LINE_MAX + 1));
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int count = Math.min(aspects.getAspects().size(), LINE_MAX);
        return count * 17 - 1;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context.push();
        context.scale(0.5f, 0.5f, 1);

        int i = 0;
        for (Map.Entry<Aspect, Integer> entry : aspects.getAspects().entrySet()) {
            Aspect aspect = entry.getKey();
            Integer value = entry.getValue();

            int xIndex = i % LINE_MAX;
            int yIndex = i / LINE_MAX;

            renderIcon(x, y, context, aspect, xIndex, yIndex);
            renderCount(textRenderer, context, x + xIndex * 17, y + yIndex * 17, value);
            i++;
        }
        context.pop();
    }

    private void renderIcon(int x, int y, DrawContext context, Aspect aspect, int xIndex, int yIndex) {
        RenderSystem.enableBlend();
        context.drawTexture(TEXTURE, x * 2 + xIndex * 34, y * 2 + yIndex * 34, aspect.getTextureMinX(), aspect.getTextureMinY(), 32, 32, EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
    }

    private static void renderCount(TextRenderer textRenderer, DrawContext context, int x, int y, int count) {
        MatrixStack matrices = new MatrixStack();
        String value = String.valueOf(count);
        matrices.translate(0.0F, 0.0F, 200.0F);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        context.drawText(textRenderer, value, x + 19 - 2 - textRenderer.getWidth(value), y + 6 + 3, 16777215, true);
        immediate.draw();
    }
}
