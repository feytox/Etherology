package ru.feytox.etherology.block.zone;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.util.misc.Color;
import ru.feytox.etherology.util.misc.EIdentifier;
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

    public static void refreshOculus(long time) {
        oculusLastTick = time;
    }

    public static void refreshRevelation(long time) {
        revelationLastTick = time;
    }

    public static void refreshZone(ZoneCoreBlockEntity blockEntity, BlockPos pos, EssenceZoneType zoneType, long time) {
        zonesData.merge(pos, new Data(blockEntity, zoneType, Color.from(zoneType.getEndColor()), time), (oldData, data) -> {
            if (!oldData.zoneType.equals(data.zoneType)) return data;
            oldData.lastTime = data.lastTime;
            return oldData;
        });
    }

    public static void registerRendering() {
        WorldRenderEvents.LAST.register(ZoneCoreRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        long time = context.world().getTime();
        if (!canSeeZone(time)) return;

        boolean seeThrough = canSeeThrough(time);

        zonesData.entrySet().removeIf(entry -> entry.getValue().blockEntity.isRemoved() || time - entry.getValue().lastTime > LIFETIME);
        zonesData.forEach((pos, data) -> data.render(context, pos, time, seeThrough));
    }

    private static boolean canSeeZone(long time) {
        return time - oculusLastTick <= VISIBLE_COOLDOWN || time - revelationLastTick <= VISIBLE_COOLDOWN;
    }

    private static boolean canSeeThrough(long time) {
        return time - oculusLastTick <= VISIBLE_COOLDOWN;
    }

    @AllArgsConstructor
    private static class Data {

        private static final int DISPERSION_COOLDOWN = 5;
        private static final int LIGHT_COOLDOWN = 10;
        private static final float DISPERSION_CHANCE = 0.2f;
        private static final float LIGHT_CHANCE = 0.15f;
        private static final int SEAL_TIMING = 10;

        private final ZoneCoreBlockEntity blockEntity;
        private final EssenceZoneType zoneType;
        private final Color color;
        private long lastTime;
        private final List<Dispersion> dispersions = new ObjectArrayList<>();
        private final List<SealLight> sealLights = new ObjectArrayList<>();

        private void render(WorldRenderContext context, BlockPos pos, long time, boolean seeThrough) {
            MatrixStack matrices = context.matrixStack();
            if (matrices == null) return;

            float tickDelta = context.tickCounter().getTickDelta(false);
            Camera camera = context.camera();
            Vec3d coreVec = pos.toCenterPos().subtract(camera.getPos());
            Random random = context.world().getRandom();

            matrices.push();
            matrices.translate(coreVec.x, coreVec.y, coreVec.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(blockEntity.getPitch()));
            matrices.scale(1 / 128f, 1 / 128f, 1 / 128f);
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            if (seeThrough) RenderSystem.disableDepthTest();

            renderSeal(matrices, random, time, tickDelta);
            renderDispersions(matrices, random, time, tickDelta);

            RenderSystem.disableBlend();
            if (seeThrough) RenderSystem.enableDepthTest();
            matrices.pop();
        }

        private void renderSeal(MatrixStack matrices, Random random, long time, float tickDelta) {
            float percent = MathHelper.TAU * ((time+tickDelta) % SEAL_TIMING) / SEAL_TIMING;
            double dx = 0.25d * Math.sin(100 * percent) + 0.3d * Math.cos(percent);
            double dy = 0.25d * Math.cos(100 * percent) + 0.3d * Math.sin(percent);

            matrices.push();
            matrices.scale(2, 2, 2);

            matrices.push();
            renderSealLights(matrices, random, time, tickDelta);
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
            for (int i = 0, sealLightsSize = sealLights.size(); i < sealLightsSize; i++) {
                SealLight sealLight = sealLights.get(i);
                sealLight.render(matrices, zoneType.getTextureLightId(), time, tickDelta, i*0.01f);
            }
        }

        private void refreshLights(Random random, long time, float tickDelta) {
            sealLights.removeIf(sealLight -> sealLight.isDead(time, tickDelta));

            if (time % LIGHT_COOLDOWN != 0) return;
            if (random.nextFloat() > LIGHT_CHANCE) return;

            int maxAge = random.nextBetween(20, 30);
            float targetDx = MathHelper.lerp(random.nextFloat(), -5, 5);
            float targetDy = MathHelper.lerp(random.nextFloat(), -5, 5);
            float targetRoll = MathHelper.lerp(random.nextFloat(), -0.15f, 0.15f);

            sealLights.add(new SealLight(maxAge, time, targetDx, targetDy, targetRoll));
        }

        private void renderDispersions(MatrixStack matrices, Random random, long time, float tickDelta) {
            refreshDispersions(random, time, tickDelta);
            dispersions.forEach(dispersion -> dispersion.render(matrices, color, time, tickDelta));
        }

        private void refreshDispersions(Random random, long time, float tickDelta) {
            dispersions.removeIf(dispersion -> dispersion.isDead(time, tickDelta));

            if (time % DISPERSION_COOLDOWN != 0) return;
            if (random.nextFloat() > DISPERSION_CHANCE) return;

            int maxAge = random.nextBetween(30, 50);
            float yaw = random.nextFloat() * MathHelper.TAU;
            float pitch = random.nextFloat() * MathHelper.TAU;
            float roll = random.nextFloat() * MathHelper.TAU;

            dispersions.add(new Dispersion(maxAge, time, yaw, pitch, roll));
        }
    }

    private record SealLight(int maxAge, long spawnTime, float targetDx, float targetDy, float targetRoll) {

        private boolean isDead(long time, float tickDelta) {
            return time - spawnTime + tickDelta > maxAge;
        }

        private void render(MatrixStack matrices, Identifier texture, long time, float tickDelta, float dz) {
            float percent = (time - spawnTime + tickDelta) / maxAge;
            float alpha = 1.0f - percent;
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

    private record Dispersion(int maxAge, long spawnTime, float yaw, float pitch, float roll) {

        private static final Identifier DISPERSION = EIdentifier.of("textures/block/seal_dispersion.png");
        private static final float MAX_SCALE = 2.0f;

        private boolean isDead(long time, float tickDelta) {
            return time - spawnTime + tickDelta > maxAge;
        }

        private void render(MatrixStack matrices, Color color, long time, float tickDelta) {
            float percent = (time - spawnTime + tickDelta) / maxAge;
            float alpha = 0.5f - Math.abs(percent - 0.5f);
            float scale = MathHelper.lerp(percent, 1.0f, MAX_SCALE);
            float angle = percent * MathHelper.TAU;

            matrices.push();
            color.applyColor(alpha);

            matrices.scale(scale, scale, scale);
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(roll));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(pitch));
            matrices.multiply(new Quaternionf().rotateLocalZ(angle));

            matrices.translate(32, 32, 0);
            RenderSystem.setShaderTexture(0, DISPERSION);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        }
    }
}
