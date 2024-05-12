package ru.feytox.etherology.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.feytox.etherology.util.misc.EIdentifier;

public class RedstoneRayRenderer<T extends LivingEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {

    private static final Identifier TEXTURE = new EIdentifier("textures/entity/redstone_stream.png");

    public RedstoneRayRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

    }

    public static void render(WorldRenderContext context) {
//        PlayerEntity entity = MinecraftClient.getInstance().player;
//        if (entity == null) return;
//
//        ItemStack stack = entity.getActiveItem();
//        if (!(stack.getItem() instanceof StaffItem)) return;
//
//        // TODO: 11.05.2024 only redstone lens
//        val lensData = EtherologyComponents.LENS.get(stack);
//        if (lensData.isEmpty() || !lensData.getLensMode().equals(LensMode.STREAM)) return;
//
//        float tickDelta = context.tickDelta();
//        MatrixStack matrices = context.matrixStack();
//
//        HitResult hitResult = entity.raycast(32.0f, tickDelta, false);
//        if (!(hitResult instanceof BlockHitResult)) return;
//
//        Vec3d hitPos = hitResult.getPos();
//        Vector3f endPos = hitPos.subtract(entity.getPos()).toVector3f();

        Camera camera = context.camera();
        MatrixStack matrices = context.matrixStack();

        Vec3d startPos = new Vec3d(61.5f, -60.5f, -63.5f);
        Vec3d pos1 = new Vec3d(61.5f, -59.5f, -60.5f);
        Vec3d pos2 = new Vec3d(66.5f, -59.5f, -63.5f);
        Vec3d pos3 = new Vec3d(61.5f, -59.5f, -69.5f);
        Vec3d pos4 = new Vec3d(50.5f, -59.5f, -63.5f);

        renderRay(matrices, camera, startPos, pos1);
        renderRay(matrices, camera, startPos, pos2);
        renderRay(matrices, camera, startPos, pos3);
        renderRay(matrices, camera, startPos, pos4);
    }

    private static void renderRay(MatrixStack matrices, Camera camera, Vec3d fromPos, Vec3d toPos) {
        Vector3f cameraPos = camera.getPos().toVector3f();
        Vector3f startPos = fromPos.toVector3f();
        Vector3f endPos = toPos.toVector3f();
        startPos.sub(cameraPos);
        endPos.sub(cameraPos);

        double dX = toPos.x - fromPos.x;
        double dY = toPos.y - fromPos.y;
        double dZ = toPos.z - fromPos.z;

        double rayYaw = Math.atan2(dZ, dX);
        double rayPitch = Math.atan2(Math.sqrt(dX * dX + dZ * dZ), dY) + Math.PI;
        float height = (float) (fromPos.distanceTo(toPos) + 0.01f);
        float width = 0.5f;

        matrices.push();

        matrices.translate(startPos.x, startPos.y, startPos.z);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float) -rayYaw));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) ((-rayPitch) - MathHelper.HALF_PI)));

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(positionMatrix, -width, 0, 0).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, height-width, height, height).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, height+width, height, height).texture(1f, 1f).next();
        buffer.vertex(positionMatrix, width, 0, 0).texture(1f, 0f).next();


//        buffer.vertex(positionMatrix, startPos.x-0.5f, startPos.y, startPos.z).texture(0f, 0f).next();
//        buffer.vertex(positionMatrix, endPos.x-0.5f, endPos.y, endPos.z).texture(0f, 1f).next();
//        buffer.vertex(positionMatrix, endPos.x+0.5f, endPos.y, endPos.z).texture(1f, 1f).next();
//        buffer.vertex(positionMatrix, startPos.x+0.5f, startPos.y, startPos.z).texture(1f, 0f).next();

        tessellator.draw();

        matrices.pop();
    }
}