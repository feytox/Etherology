package ru.feytox.etherology.util.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

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
}
