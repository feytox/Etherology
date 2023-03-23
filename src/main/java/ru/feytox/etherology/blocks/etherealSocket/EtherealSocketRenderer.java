package ru.feytox.etherology.blocks.etherealSocket;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import static ru.feytox.etherology.blocks.etherealSocket.EtherealSocketBlock.WITH_GLINT;

public class EtherealSocketRenderer implements BlockEntityRenderer<EtherealSocketBlockEntity> {
    private static final float SCALE = 0.06692f;

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

        vertexConsumers.getBuffer(RenderLayer.getSolid()).light(0);

        // side north
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        prepareTranslate(matrices);
        setTexture("textures/block/socket_glint_side.png", percent);
        drawTexture(matrices, 4, 8);
        matrices.pop();

        //side south
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, 180);
        prepareTranslate(matrices);
        setTexture("textures/block/socket_glint_side.png", percent);
        drawTexture(matrices, 4, 8);
        matrices.pop();

        //side west
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, 90);
        prepareTranslate(matrices);
        setTexture("textures/block/socket_glint_side.png", percent);
        drawTexture(matrices, 4, 8);
        matrices.pop();

        //side east
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_Y, 270);
        prepareTranslate(matrices);
        setTexture("textures/block/socket_glint_side.png", percent);
        drawTexture(matrices, 4, 8);
        matrices.pop();

        //side top
        matrices.push();
        prepareRender(matrices, angleX, angleY, angleZ);
        centerRotation(matrices, RotationAxis.POSITIVE_X, 90);
        prepareTranslate(matrices);
        matrices.translate(0, 1.85, -1.89);
        setTexture("textures/block/socket_glint_top.png", percent);
        drawTexture(matrices, 4, 4);
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

    private static void setTexture(String texturePath, float percent) {
        int textNum = MathHelper.floor(percent * 16);
        texturePath = texturePath.replace(".png", "_" + textNum + ".png");

        RenderSystem.setShader(GameRenderer::getBlockProgram);
        RenderSystem.setShaderTexture(0, new EIdentifier(texturePath));
        RenderSystem.enableDepthTest();
    }

    private static void drawTexture(MatrixStack matrices, int width, int height) {
        DrawableHelper.drawTexture(matrices, 0, 0, 0, width/16f, height/16f, width, height, 16, 16);
    }
}
