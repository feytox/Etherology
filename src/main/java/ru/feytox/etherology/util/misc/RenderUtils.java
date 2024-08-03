package ru.feytox.etherology.util.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@UtilityClass
public class RenderUtils {

    public static void renderTexture(DrawContext context, float x0, float y0, int u, int v, float width, float height, int regionWidth, int regionHeight) {
        renderTexture(context, x0, y0, u, v, width, height, regionWidth, regionHeight, regionWidth, regionHeight);
    }

    public static void renderTexture(MatrixStack matrices, float x0, float y0, int u, int v, float width, float height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        val matrix = matrices.peek().getPositionMatrix();
        float x1 = x0 + width;
        float y1 = y0 + height;
        float u0 = (float) u / textureWidth;
        float v0 = (float) v / textureHeight;
        float u1 = (float) (u + regionWidth) / textureWidth;
        float v1 = (float) (v + regionHeight) / textureHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, 0).texture(u0, v1);
        bufferBuilder.vertex(matrix, x1, y1, 0).texture(u1, v1);
        bufferBuilder.vertex(matrix, x1, y0, 0).texture(u1, v0);
        bufferBuilder.vertex(matrix, x0, y0, 0).texture(u0, v0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void renderTexture(MatrixStack matrices, float x0, float y0, float z, int u, int v, float width, float height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        val matrix = matrices.peek().getPositionMatrix();
        float x1 = x0 - width;
        float y1 = y0 - height;
        float u0 = (float) u / textureWidth;
        float v0 = (float) v / textureHeight;
        float u1 = (float) (u + regionWidth) / textureWidth;
        float v1 = (float) (v + regionHeight) / textureHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, z).texture(u0, v1);
        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1);
        bufferBuilder.vertex(matrix, x1, y0, z).texture(u1, v0);
        bufferBuilder.vertex(matrix, x0, y0, z).texture(u0, v0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void renderTexture(DrawContext context, float x0, float y0, float z, int u, int v, float width, float height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        renderTexture(context.getMatrixStack(), x0, y0, z, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public static void renderTexture(DrawContext context, float x0, float y0, int u, int v, float width, float height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        renderTexture(context.getMatrixStack(), x0, y0, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public void drawStraightLine(DrawContext context, float x1, float y1, float x2, float y2, float width, int color) {
        if (x1 == x2) drawVerticalLine(context, x1, y1, y2, width, color);
        if (y1 == y2) drawHorizontalLine(context, x1, x2, y1, width, color);
    }

    /**
     * @see DrawContext#drawHorizontalLine(RenderLayer, int, int, int, int) 
     */
    public void drawHorizontalLine(DrawContext context, float x1, float x2, float y, float width, int color) {
        if (x2 < x1) {
            float i = x1;
            x1 = x2;
            x2 = i;
        }

        fill(context, x1 - width/2, y - width/2, x2 + width/2, y + width/2, color);
    }

    /**
     * @see DrawContext#drawVerticalLine(RenderLayer, int, int, int, int)
     */
    public void drawVerticalLine(DrawContext context, float x, float y1, float y2, float width, int color) {
        if (y2 < y1) {
            float i = y1;
            y1 = y2;
            y2 = i;
        }

        fill(context, x - width/2, y1 + width/2, x + width/2, y2 - width/2, color);
    }
    
    /**
     * @see DrawContext#fill(RenderLayer, int, int, int, int, int, int)
     */
    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }


        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, x1, y1, 0).color(color);
        vertexConsumer.vertex(matrix4f, x1, y2, 0).color(color);
        vertexConsumer.vertex(matrix4f, x2, y2, 0).color(color);
        vertexConsumer.vertex(matrix4f, x2, y1, 0).color(color);
        context.draw();
    }

    /**
     * @see DrawContext#drawItem(ItemStack, int, int)
     */
    public static void drawItem(DrawContext context, MinecraftClient client, ItemStack stack, float x, float y) {
        drawItem(context, client.player, client.world, stack, x, y, 0, 0);
    }

    /**
     * @see DrawContext#drawItem(LivingEntity, World, ItemStack, int, int, int, int)
     */
    public static void drawItem(DrawContext context, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, float x, float y, int seed, int z) {
        if (stack.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        BakedModel bakedModel = client.getItemRenderer().getModel(stack, world, entity, seed);
        context.push();
        context.translate(x + 8, y + 8, (float)(150 + (bakedModel.hasDepth() ? z : 0)));

        try {
            context.scale(16.0F, -16.0F, 16.0F);
            boolean bl = !bakedModel.isSideLit();
            if (bl) {
                DiffuseLighting.disableGuiDepthLighting();
            }

            client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, context.getMatrices(), context.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
            context.draw();
            if (bl) {
                DiffuseLighting.enableGuiDepthLighting();
            }
        } catch (Throwable var12) {
            CrashReport crashReport = CrashReport.create(var12, "Rendering item");
            CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
            crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
            crashReportSection.add("Item Components", () -> String.valueOf(stack.getComponents()));
            crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
            throw new CrashException(crashReport);
        }

        context.pop();
    }

}
