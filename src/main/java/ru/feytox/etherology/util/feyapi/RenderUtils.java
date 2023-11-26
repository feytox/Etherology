package ru.feytox.etherology.util.feyapi;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

@UtilityClass
public class RenderUtils {

    public static void renderTexture(MatrixStack matrices, float x0, float y0, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        renderTexture(matrices, x0, y0, u, v, width, height, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public static void renderTexture(MatrixStack matrices, float x0, float y0, int u, int v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        val matrix = matrices.peek().getPositionMatrix();
        float x1 = x0 + width;
        float y1 = y0 + height;
        float u0 = (float) u / textureWidth;
        float v0 = (float) v / textureHeight;
        float u1 = (float) (u + regionWidth) / textureWidth;
        float v1 = (float) (v + regionHeight) / textureHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x0, y1, 0).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, x1, y1, 0).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y0, 0).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, x0, y0, 0).texture(u0, v0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

}
