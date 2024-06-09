package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import static ru.feytox.etherology.gui.staff.StaffLensesScreen.ITEM_RADIUS;
import static ru.feytox.etherology.gui.staff.StaffLensesScreen.LENS_OPEN_DELAY;

@RequiredArgsConstructor
public class LensWidget extends DrawableHelper {

    private final ItemStack stack;
    private final Float angle;

    private boolean selected = false;
    private boolean isClosing = false;
    private float lensTicks = 0.0f;

    /**
     * @see io.wispforest.owo.ui.component.ItemComponent#draw(MatrixStack, int, int, float, float)
     */
    public void render(StaffLensesScreen parent, VertexConsumerProvider.Immediate immediate, MatrixStack matrices, ItemRenderer itemRenderer, float centerX, float centerY, float progress, float circleScale) {
        if (stack == null || stack.isEmpty()) return;

        float dx = centerX;
        float dy = centerY;
        if (angle != null) {
            dx += getX(progress, circleScale);
            dy += getY(progress, circleScale);
        }

        float selectionScale = getLensScale();
        draw(immediate, itemRenderer, progress*selectionScale, dx, dy);
        renderItemBar(matrices, progress, dx, dy);

        if (!selected) return;
        matrices.push();
        matrices.translate(dx - 8.0f * progress, dy - 8.0f * progress, 0);
        parent.renderTooltip(matrices, parent.getTooltipFromItem(stack), 0, 0);
        matrices.pop();
    }

    private void draw(VertexConsumerProvider.Immediate immediate, ItemRenderer itemRenderer, float progress, float dx, float dy) {
        boolean notSideLit = itemRenderer.getModel(stack, null, null, 0).isSideLit();
        if (notSideLit) DiffuseLighting.disableGuiDepthLighting();

        DiffuseLighting.disableGuiDepthLighting();
        MatrixStack modelView = RenderSystem.getModelViewStack();
        modelView.push();

        modelView.translate(dx, dy, 100);
        modelView.scale(progress, progress, progress);
        modelView.scale(16, -16, 16);

        RenderSystem.applyModelViewMatrix();
        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, new MatrixStack(), immediate, 0);
        immediate.draw();

        modelView.pop();
        RenderSystem.applyModelViewMatrix();
        if (notSideLit) DiffuseLighting.enableGuiDepthLighting();
    }

    private void renderItemBar(MatrixStack matrices, float progress, float dx, float dy) {
        if (!stack.isItemBarVisible()) return;
        matrices.push();
        matrices.translate(dx, dy, 100);
        matrices.scale(progress, progress, progress);
        matrices.translate(-8, -8, 0);

        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int barStep = stack.getItemBarStep();
        int barColor = stack.getItemBarColor();

        val matrix = matrices.peek().getPositionMatrix();
        renderGuiQuad(bufferBuilder, matrix, 13, 2, 0, 0, 0);
        renderGuiQuad(bufferBuilder, matrix, barStep, 1, barColor >> 16 & 255, barColor >> 8 & 255, barColor & 255);
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();

        matrices.pop();
    }

    private void renderGuiQuad(BufferBuilder buffer, Matrix4f matrix, int width, int height, int red, int green, int blue) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, 2, 13, 0).color(red, green, blue, 255).next();
        buffer.vertex(matrix, 2, 13 + height, 0).color(red, green, blue, 255).next();
        buffer.vertex(matrix, 2 + width, 13 + height, 0).color(red, green, blue, 255).next();
        buffer.vertex(matrix, 2 + width, 13, 0).color(red, green, blue, 255).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public ItemStack updateMouse(int mouseX, int mouseY, float centerX, float centerY, float progress, float circleScale) {
        boolean result = isHovered(mouseX, mouseY, centerX, centerY, progress, circleScale);
        if (result == selected) return result ? stack : null;
        lensTicks = isClosing ? LENS_OPEN_DELAY - lensTicks : 0.0f;
        isClosing = selected;
        selected = result;
        return result ? stack : null;
    }

    private boolean isHovered(int mouseX, int mouseY, float centerX, float centerY, float progress, float circleScale) {
        float dx = getX(progress, circleScale) - 8.0f * progress;
        float dy = getY(progress, circleScale) - 8.0f * progress;
        return StaffLensesScreen.isInBox(mouseX, mouseY, centerX+dx, centerY+dy, 16.0f*progress, 16.0f*progress);
    }

    private float getX(float progress, float circleScale) {
        return ITEM_RADIUS * progress * circleScale * MathHelper.cos(angle);
    }

    private float getY(float progress, float circleScale) {
        return ITEM_RADIUS * progress * circleScale * MathHelper.sin(angle);
    }

    public void tick(float delta) {
        if (!selected && !isClosing || angle == null) return;
        lensTicks = Math.min(LENS_OPEN_DELAY, lensTicks + delta);
        if (isClosing && lensTicks == LENS_OPEN_DELAY) {
            isClosing = false;
            lensTicks = 0.0f;
        }
    }

    private float getLensScale() {
        float scale = 1.0f;
        if (!selected && !isClosing || angle == null) return scale;
        float progress = lensTicks / LENS_OPEN_DELAY;
        progress = isClosing ? 1-progress : progress;
        progress = (1 - MathHelper.cos(MathHelper.PI * progress)) / 6;
        return scale + progress;
    }
}
