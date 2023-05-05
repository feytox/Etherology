package ru.feytox.etherology.block.etherealSocket;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import static ru.feytox.etherology.block.etherealSocket.EtherealSocketBlock.WITH_GLINT;

public class EtherealSocketRenderer implements BlockEntityRenderer<EtherealSocketBlockEntity> {
    private static final float SCALE = 0.0625f;
    private static final String GLINT_SIDE_PATH = "textures/block/socket_glint_side.png";
    private static final String GLINT_TOP_PATH = "textures/block/socket_glint_top.png";

    public EtherealSocketRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(EtherealSocketBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState cachedState = entity.getCachedState();
        if (!cachedState.get(WITH_GLINT)) return;
        float percent = entity.getCachedPercent();

        Direction direction = cachedState.get(FacingBlock.FACING);
        if (direction == null) return;

        float angleX = 0;
        float angleY = 0;
        float angleZ = 0;
        switch (direction) {
            case NORTH -> angleX = 270;
            case EAST -> {
                angleX = 90;
                angleZ = 270;
            }
            case SOUTH -> {
                angleX = 90;
                angleY = 180;
            }
            case WEST -> {
                angleX = 90;
                angleZ = 90;
            }
            case UP -> angleX = 180;
        }

        // side north
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        prepareTranslate(matrices);
        renderTexture(matrices, vertexConsumers, GLINT_SIDE_PATH, light, overlay, 4, 8, percent);
        matrices.pop();

        //side south
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, 180);
        prepareTranslate(matrices);
        renderTexture(matrices, vertexConsumers, GLINT_SIDE_PATH, light, overlay, 4, 8, percent);
        matrices.pop();

        //side west
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, 90);
        prepareTranslate(matrices);
        renderTexture(matrices, vertexConsumers, GLINT_SIDE_PATH, light, overlay, 4, 8, percent);
        matrices.pop();

        //side east
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, 270);
        prepareTranslate(matrices);
        renderTexture(matrices, vertexConsumers, GLINT_SIDE_PATH, light, overlay, 4, 8, percent);
        matrices.pop();

        //side top
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_X, 90);
        prepareTranslate(matrices);
        matrices.translate(0, 2, -2);
        renderTexture(matrices, vertexConsumers, GLINT_TOP_PATH, light, overlay, 4, 4, percent);
        matrices.pop();
    }

    private static void centerRotation(MatrixStack matrices, RotationAxis axis, float deg) {
        centerRotation(matrices, axis, deg, SCALE);
    }

    private static void centerRotation(MatrixStack matrices, RotationAxis axis, float deg, float scale) {
        float offset = (1 / scale) / 2;
        matrices.translate(offset, offset, offset);
        matrices.multiply(axis.rotationDegrees(deg));
        matrices.translate(-offset, -offset, -offset);
    }

    private static void prepareRender(MatrixStack matrices, float angleX, float angleY, float angleZ) {
        centerRotation(matrices, RotationAxis.POSITIVE_X, angleX, 1);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, angleY, 1);
        centerRotation(matrices, RotationAxis.POSITIVE_Z, angleZ, 1);
        matrices.scale(SCALE, SCALE, SCALE);
    }

    private static void prepareTranslate(MatrixStack matrices) {
        float k = 1 / SCALE;
        matrices.translate(0.375 * k, 0.25 * k, 0.375 * k);
    }

    public static void renderTexture(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String texturePath, int light, int overlay, int width, int height, float percent) {
        int textNum = MathHelper.floor(percent * 16);
        texturePath = texturePath.replace(".png", "_" + textNum + ".png");
        Identifier texture = new EIdentifier(texturePath);

        float u2 = width / 16f;
        float v2 = height / 16f;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f modelMatrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShaderTexture(0, texture);

        int blockLight = LightmapTextureManager.getBlockLightCoordinates(light);
        int skyLight = LightmapTextureManager.getSkyLightCoordinates(light);
        light = LightmapTextureManager.pack(
                blockLight + MathHelper.floor(15 * percent - blockLight),
                skyLight);

        vertexConsumer.vertex(modelMatrix, 0.0f, (float) height, 0.0f)
                .color(255, 255, 255, 255).texture(0.0f, v2)
                .overlay(overlay).light(light).normal(0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(modelMatrix, (float) width, (float) height, 0.0f)
                .color(255, 255, 255, 255).texture(u2, v2)
                .overlay(overlay).light(light).normal(0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(modelMatrix, (float) width, 0.0f, 0.0f)
                .color(255, 255, 255, 255).texture(u2, 0.0f)
                .overlay(overlay).light(light).normal(0.0f, 0.0f, 1.0f).next();
        vertexConsumer.vertex(modelMatrix, 0.0f, 0.0f, 0.0f)
                .color(255, 255, 255, 255).texture(0.0f, 0.0f)
                .overlay(overlay).light(light).normal(0.0f, 0.0f, 1.0f).next();
    }
}
