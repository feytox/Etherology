package ru.feytox.etherology.block.zone;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class ZoneCoreRenderer {

    private static final int VISIBLE_COOLDOWN = 4;
    public static final int LIFETIME = 100;
    private static final Identifier TEXTURE = EIdentifier.of("textures/block/zone_core.png");
    private static final Map<BlockPos, Long> zonesData = new Object2LongOpenHashMap<>();
    private static long oculusLastTick;
    private static long revelationLastTick;

    public static void refreshOculus(long time) {
        oculusLastTick = time;
    }

    public static void refreshRevelation(long time) {
        revelationLastTick = time;
    }

    public static void refreshZone(BlockPos pos, long time) {
        zonesData.put(pos, time);
    }

    public static void registerRendering() {
        WorldRenderEvents.LAST.register(ZoneCoreRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        long time = context.world().getTime();
        if (!canSeeZone(time)) return;

        boolean seeThrough = canSeeThrough(time);

        zonesData.entrySet().removeIf(entry -> time - entry.getValue() > LIFETIME);
        zonesData.forEach((pos, lastTime) -> renderCore(context, pos, seeThrough));
    }

    private static void renderCore(WorldRenderContext context, BlockPos pos, boolean seeThrough) {
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        Camera camera = context.camera();
        Vec3d coreVec = pos.toCenterPos().subtract(camera.getPos());

        matrices.push();

        matrices.translate(coreVec.x, coreVec.y, coreVec.z);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.translate(0.5f, 0.5f, 0f);
        matrices.scale(0.0625f, 0.0625f, 0.0625f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (seeThrough) RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 16, 16, 16, 16, 16, 16);
        if (seeThrough) RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    private static boolean canSeeZone(long time) {
        return time - oculusLastTick <= VISIBLE_COOLDOWN || time - revelationLastTick <= VISIBLE_COOLDOWN;
    }

    private static boolean canSeeThrough(long time) {
        return time - oculusLastTick <= VISIBLE_COOLDOWN;
    }
}
