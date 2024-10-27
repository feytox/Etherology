package ru.feytox.etherology.gui.staff;

import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;

import static ru.feytox.etherology.gui.staff.StaffLensesScreen.ITEM_RADIUS;
import static ru.feytox.etherology.gui.staff.StaffLensesScreen.LENS_OPEN_DELAY;

public class LensWidget {

    private final ItemStack stack;
    private final Float angle;
    private final ItemRenderer itemRenderer;

    private boolean selected = false;
    private boolean isClosing = false;
    private float lensTicks = 0.0f;

    public LensWidget(ItemStack stack, Float angle) {
        this.stack = stack;
        this.angle = angle;
        itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    public void render(StaffLensesScreen parent, VertexConsumerProvider.Immediate immediate, DrawContext context, float centerX, float centerY, float progress, float circleScale) {
        if (stack == null || stack.isEmpty()) return;

        float dx = centerX;
        float dy = centerY;
        if (angle != null) {
            dx += getX(progress, circleScale);
            dy += getY(progress, circleScale);
        }

        float selectionScale = getLensScale();
        draw(context, immediate, progress*selectionScale, dx, dy);
        renderItemBar(context, progress, dx, dy);

        if (!selected) return;
        context.push();
        context.translate(dx - 8.0f * progress, dy - 8.0f * progress, 0);
        parent.renderTooltip(context, stack);
        context.pop();
    }

    /**
     * @see io.wispforest.owo.ui.component.ItemComponent#draw(OwoUIDrawContext, int, int, float, float)
     */
    private void draw(DrawContext context, VertexConsumerProvider.Immediate immediate, float progress, float dx, float dy) {
        DiffuseLighting.disableGuiDepthLighting();
        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(dx, dy, 100);
        matrices.scale(progress, progress, progress);
        matrices.scale(16, -16, 16);

        itemRenderer.renderItem(this.stack, ModelTransformationMode.GUI, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrices, immediate, MinecraftClient.getInstance().world, 0);
        immediate.draw();
        matrices.pop();

        DiffuseLighting.enableGuiDepthLighting();
    }

    /**
     * @see DrawContext#drawItemInSlot(TextRenderer, ItemStack, int, int, String) 
     */
    private void renderItemBar(DrawContext context, float progress, float dx, float dy) {
        if (!stack.isItemBarVisible()) return;
        context.push();
        context.translate(dx, dy, 100);
        context.scale(progress, progress, progress);
        context.translate(-8, -8, 0);

        int barStep = stack.getItemBarStep();
        int barColor = stack.getItemBarColor();

        context.fill(RenderLayer.getGuiOverlay(), 2, 13, 15, 15, Colors.BLACK);
        context.fill(RenderLayer.getGuiOverlay(), 2, 13, 2+barStep, 14, barColor | Colors.BLACK);

        context.pop();
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
