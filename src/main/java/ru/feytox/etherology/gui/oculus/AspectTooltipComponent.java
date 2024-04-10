package ru.feytox.etherology.gui.oculus;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
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
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Map;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

public class AspectTooltipComponent implements TooltipComponent {
    private static final Identifier TEXTURE = new EIdentifier("textures/gui/aspects.png");
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
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        matrices.push();
        matrices.scale(0.5f, 0.5f, 1);

        int i = 0;
        for (Map.Entry<Aspect, Integer> entry : aspects.getAspects().entrySet()) {
            Aspect aspect = entry.getKey();
            Integer value = entry.getValue();

            int xIndex = i % LINE_MAX;
            int yIndex = i / LINE_MAX;

            renderIcon(x, y, z, matrices, aspect, xIndex, yIndex);
            renderCount(textRenderer, itemRenderer, x + xIndex * 17, y + yIndex * 17, value);
            i++;
        }
        matrices.pop();
    }

    private void renderIcon(int x, int y, int z, MatrixStack matrices, Aspect aspect, int xIndex, int yIndex) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();

        drawTexture(matrices, x * 2 + xIndex * 34, y * 2 + yIndex * 34, z, aspect.getTextureMinX(), aspect.getTextureMinY(), 32, 32, EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
    }

    private static void renderCount(TextRenderer textRenderer, ItemRenderer itemRenderer, int x, int y, int count) {
        MatrixStack matrices = new MatrixStack();
        String value = String.valueOf(count);
        matrices.translate(0.0F, 0.0F, itemRenderer.zOffset + 200.0F);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        textRenderer.draw(value, x + 17 - textRenderer.getWidth(value), y + 9, 16777215, true, matrices.peek().getPositionMatrix(), immediate, false, 0, 15728880);
        immediate.draw();
    }
}
