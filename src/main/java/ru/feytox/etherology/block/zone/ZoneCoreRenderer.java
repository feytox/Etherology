package ru.feytox.etherology.block.zone;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ZoneCoreRenderer {

    private static final Map<BlockPos, Data> zonesData = new Object2ObjectOpenHashMap<>();
    private static final int VISIBLE_COOLDOWN = 4;
    public static final int LIFETIME = 100;
    private static long oculusLastTick;
    private static long revelationLastTick;
    private static World lastWorld;

    public static void refreshOculus(long time) {
        oculusLastTick = time;
    }

    public static void refreshRevelation(long time) {
        revelationLastTick = time;
    }

    public static void refreshZone(ZoneCoreBlockEntity blockEntity, BlockPos pos, EssenceZoneType zoneType, long time) {
        zonesData.merge(pos, new Data(blockEntity, zoneType).setTime(time), (oldData, data) -> {
            if (!oldData.zoneType.equals(data.zoneType)) return data;
            oldData.setTime(data.lastTime);
            return oldData;
        });
    }

    public static void registerRendering() {
        WorldRenderEvents.LAST.register(ZoneCoreRenderer::render);
    }

    public static void tickDataResetting(MinecraftClient client) {
        if (lastWorld == client.world) return;

        lastWorld = client.world;
        zonesData.clear();
    }

    private static void render(WorldRenderContext context) {
        long time = context.world().getTime();
        if (!canSeeZone(time)) return;

        boolean seeThrough = canSeeThrough(time);

        zonesData.entrySet().removeIf(entry -> entry.getValue().zoneCore.isRemoved() || time - entry.getValue().lastTime > LIFETIME);
        zonesData.forEach((pos, data) -> data.render(context, pos, time, seeThrough));
    }

    private static boolean canSeeZone(long time) {
        return time - oculusLastTick <= VISIBLE_COOLDOWN || time - revelationLastTick <= VISIBLE_COOLDOWN;
    }

    private static boolean canSeeThrough(long time) {
        return time - oculusLastTick <= VISIBLE_COOLDOWN;
    }

    @RequiredArgsConstructor
    private static class Data {

        private static final float LIGHT_CHANCE = 0.05f;
        private static final int SEAL_TIMING = 10;

        private final ZoneCoreBlockEntity zoneCore;
        private final EssenceZoneType zoneType;
        private long lastTime;
        private long lastLightTime;
        private final List<SealLight> sealLights = new ObjectArrayList<>();

        private Data setTime(long lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        private void render(WorldRenderContext context, BlockPos pos, long time, boolean seeThrough) {
            MatrixStack matrices = context.matrixStack();
            if (matrices == null) return;

            float scale = 1/100f * MathHelper.lerp(zoneCore.getScale(), 1.0f, 2.0f);
            float tickDelta = context.tickCounter().getTickDelta(false);
            Camera camera = context.camera();
            Vec3d coreVec = pos.toCenterPos().subtract(camera.getPos());
            Random random = context.world().getRandom();

            matrices.push();
            matrices.translate(coreVec.x, coreVec.y, coreVec.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(zoneCore.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(zoneCore.getPitch()));
            matrices.scale(scale, scale, scale);
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);

            renderSeal(matrices, random, time, tickDelta, seeThrough);

            RenderSystem.disableBlend();
            matrices.pop();
        }

        private void renderSeal(MatrixStack matrices, Random random, long time, float tickDelta, boolean seeThrough) {
            float percent = MathHelper.TAU * ((time+tickDelta) % SEAL_TIMING) / SEAL_TIMING;
            double dx = 0.25d * Math.sin(100 * percent) + 0.3d * Math.cos(percent);
            double dy = 0.25d * Math.cos(100 * percent) + 0.3d * Math.sin(percent);

            matrices.push();
            matrices.scale(2, 2, 2);

            matrices.push();
            if (seeThrough) RenderSystem.disableDepthTest();
            renderSealLights(matrices, random, time, tickDelta);
            if (seeThrough) RenderSystem.enableDepthTest();
            matrices.pop();

            RenderSystem.setShaderTexture(0, zoneType.getTextureId());

            matrices.push();
            matrices.translate(dx+32, dy+32, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);
            matrices.pop();

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathHelper.PI));
            matrices.translate(dx+32, dy+32, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);
            matrices.pop();

            matrices.pop();
        }

        private void renderSealLights(MatrixStack matrices, Random random, long time, float tickDelta) {
            refreshLights(random, time, tickDelta);
            float fillPercent = zoneCore.getFillPercent();
            for (int i = 0, sealLightsSize = sealLights.size(); i < sealLightsSize; i++) {
                SealLight sealLight = sealLights.get(i);
                sealLight.render(matrices, zoneType.getTextureLightId(), time, fillPercent, tickDelta, i*0.01f);
            }
        }

        private void refreshLights(Random random, long time, float tickDelta) {
            sealLights.removeIf(sealLight -> sealLight.isDead(time, tickDelta));

            if (time == lastLightTime) return;
            if (random.nextFloat() > LIGHT_CHANCE) return;

            int maxAge = random.nextBetween(20, 30);
            float targetDx = MathHelper.lerp(random.nextFloat(), -5, 5);
            float targetDy = MathHelper.lerp(random.nextFloat(), -5, 5);
            float targetRoll = MathHelper.lerp(random.nextFloat(), -0.15f, 0.15f);
            lastLightTime = time;

            sealLights.add(new SealLight(maxAge, time, targetDx, targetDy, targetRoll));
        }
    }

    private record SealLight(int maxAge, long spawnTime, float targetDx, float targetDy, float targetRoll) {

        private boolean isDead(long time, float tickDelta) {
            return time - spawnTime + tickDelta > maxAge;
        }

        private void render(MatrixStack matrices, Identifier texture, long time, float fillPercent, float tickDelta, float dz) {
            float percent = (time - spawnTime + tickDelta) / maxAge;
            float alpha = Math.max(0.0f, fillPercent - percent);
            float scale = MathHelper.lerp(percent, 0.9f, 0.75f);

            matrices.push();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

            matrices.scale(scale, scale, scale);
            RenderSystem.setShaderTexture(0, texture);
            renderFace(matrices, percent, dz);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathHelper.PI));
            renderFace(matrices, percent, dz);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }

        private void renderFace(MatrixStack matrices, float percent, float dz) {
            matrices.push();
            matrices.translate(targetDx * percent, targetDy + percent, 0.1+dz);
            matrices.multiply(new Quaternionf().rotateLocalZ(targetRoll * percent));
            matrices.translate(32, 32, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);
            matrices.pop();
        }
    }
}
