package ru.feytox.etherology.client.block.seal;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import ru.feytox.etherology.block.seal.SealBlockEntity;
import ru.feytox.etherology.client.util.RenderUtils;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SealBlockRenderer {

    private static final int VISIBLE_COOLDOWN = 1;
    public static final int LIFETIME = 100;
    private static final int MIN_SIMPLIFIED_DISTANCE = 2 * 16;
    private static final int MID_SIMPLIFIED_DISTANCE = 4 * 16;
    private static final int MAX_DISTANCE = 16 * 16;

    private static final Map<BlockPos, Data> sealsData = new Object2ObjectOpenHashMap<>();
    private static long seeSealsLastTick = -VISIBLE_COOLDOWN;
    private static long seeThroughLastTick = -VISIBLE_COOLDOWN;
    private static long simplifiedLastTick = -VISIBLE_COOLDOWN;
    private static World lastWorld;

    public static void refreshSeeSealsAbility(long time, boolean seeThrough, boolean simplified) {
        seeSealsLastTick = time;
        if (seeThrough)
            seeThroughLastTick = time;
        if (simplified)
            simplifiedLastTick = time;
    }

    public static void refreshSeal(SealBlockEntity blockEntity, BlockPos pos, SealType sealType, long time) {
        if (sealsData.containsKey(pos)) {
            sealsData.get(pos).refresh(time);
            return;
        }

        sealsData.put(pos, new Data(blockEntity, sealType).refresh(time));
    }

    public static void registerRendering() {
        WorldRenderEvents.LAST.register(SealBlockRenderer::render);
    }

    public static void tickDataResetting(MinecraftClient client) {
        if (lastWorld == client.world) return;

        lastWorld = client.world;
        sealsData.clear();
        seeSealsLastTick = -VISIBLE_COOLDOWN;
        seeThroughLastTick = -VISIBLE_COOLDOWN;
    }

    private static void render(WorldRenderContext context) {
        var time = context.world().getTime();
        if (!canSeeSeal(time)) return;

        // force disable depth test to prevent bugs
        RenderSystem.enableDepthTest();
        var seeThrough = canSeeThrough(time);
        var isSimplified = isSimplified(time);

        sealsData.entrySet().removeIf(entry -> entry.getValue().shouldRemove(time));
        sealsData.forEach((pos, data) -> data.render(context, pos, time, seeThrough, isSimplified));
    }

    private static boolean canSeeSeal(long time) {
        return checkCooldown(seeSealsLastTick, time);
    }

    private static boolean canSeeThrough(long time) {
        return checkCooldown(seeThroughLastTick, time);
    }

    private static boolean isSimplified(long time) {
        return checkCooldown(simplifiedLastTick, time);
    }

    private static boolean checkCooldown(long lastTick, long time) {
        return time - lastTick <= VISIBLE_COOLDOWN;
    }

    @RequiredArgsConstructor
    private static class Data {

        private static final float LIGHT_CHANCE = 0.05f;
        private static final int SEAL_TIMING = 10;
        private static final int AURA_ANIMATION_TIME = 40;

        private static final Identifier[] AURA_TEXTURES;

        private final SealBlockEntity seal;
        private final SealType sealType;
        private long lastTime;
        private long lastLightTime;
        private int manhattanPlayerDistance;
        private final List<SealLight> sealLights = new ObjectArrayList<>();

        private Data refresh(long lastTime) {
            this.lastTime = lastTime;
            refreshDistance();
            return this;
        }

        private boolean shouldRemove(long time) {
            return seal.isRemoved() || time - lastTime > LIFETIME || manhattanPlayerDistance > MAX_DISTANCE;
        }

        private void refreshDistance() {
            var player = MinecraftClient.getInstance().player;
            manhattanPlayerDistance = player == null ? 0 : player.getBlockPos().getManhattanDistance(seal.getPos());
        }

        private void render(WorldRenderContext context, BlockPos pos, long time, boolean seeThrough, boolean simplified) {
            var matrices = context.matrixStack();
            if (matrices == null)
                return;

            var random = context.world().getRandom();
            var tickDelta = context.tickCounter().getTickDelta(false);

            matrices.push();
            applyTransform(context, pos, simplified, matrices);

            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);

            if (simplified)
                renderSimplifiedSeal(matrices, time, tickDelta, seeThrough);
            else
                renderSeal(matrices, random, time, tickDelta, seeThrough);

            RenderSystem.disableBlend();
            matrices.pop();
        }

        private void applyTransform(WorldRenderContext context, BlockPos pos, boolean simplified, MatrixStack matrices) {
            var baseScale = simplified ? 2.1f * seal.getRadius() : MathHelper.lerp(seal.getMaxPointsScale(), 1.0f, 2.0f);
            var scale = 1 / 100f * baseScale;
            var camera = context.camera();
            var coreVec = pos.toCenterPos().subtract(camera.getPos());

            matrices.translate(coreVec.x, coreVec.y, coreVec.z);
            if (simplified)
                matrices.multiply(camera.getRotation());
            else {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(seal.getYaw()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(seal.getPitch()));
            }
            matrices.scale(scale, scale, scale);
        }

        private void renderSimplifiedSeal(MatrixStack matrices, long time, float tickDelta, boolean seeThrough) {
            var alpha = seal.getFillPercent() * getDistancePercent();

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathHelper.PI));
            matrices.translate(64, 64, 0);
            matrices.scale(2, 2, 2);

            if (seeThrough) RenderSystem.disableDepthTest();

            RenderSystem.setShaderTexture(0, getAuraTexture(time, tickDelta));
            setSimpleSealColor(alpha);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);

            if (seeThrough) RenderSystem.enableDepthTest();
            matrices.pop();
        }

        private float getDistancePercent() {
            if (manhattanPlayerDistance > MID_SIMPLIFIED_DISTANCE)
                return 1.0f;

            if (manhattanPlayerDistance < MIN_SIMPLIFIED_DISTANCE)
                return 0.0f;

            return (float) (manhattanPlayerDistance - MIN_SIMPLIFIED_DISTANCE) / (MID_SIMPLIFIED_DISTANCE - MIN_SIMPLIFIED_DISTANCE);
        }

        private Identifier getAuraTexture(long time, float tickDelta) {
            var percent = ((time + tickDelta) % AURA_ANIMATION_TIME) / AURA_ANIMATION_TIME;
            var index = Math.round(percent * (AURA_TEXTURES.length - 1));
            return AURA_TEXTURES[index];
        }

        private void setSimpleSealColor(float alpha) {
            switch (sealType) {
                case RELLA -> RenderUtils.applyColor(0x00FF00, alpha);
                case CLOS -> RenderUtils.applyColor(0xFFCC00, alpha);
                case VIA -> RenderUtils.applyColor(0xFF0033, alpha);
                case KETA -> RenderUtils.applyColor(0x0099FF, alpha);
            }
        }

        private void renderSeal(MatrixStack matrices, Random random, long time, float tickDelta, boolean seeThrough) {
            var percent = MathHelper.TAU * ((time + tickDelta) % SEAL_TIMING) / SEAL_TIMING;
            var dx = 0.25d * Math.sin(100 * percent) + 0.3d * Math.cos(percent);
            var dy = 0.25d * Math.cos(100 * percent) + 0.3d * Math.sin(percent);

            matrices.push();
            matrices.scale(2, 2, 2);

            matrices.push();
            if (seeThrough) RenderSystem.disableDepthTest();
            renderSealLights(matrices, random, time, tickDelta);
            if (seeThrough) RenderSystem.enableDepthTest();
            matrices.pop();

            RenderSystem.setShaderTexture(0, sealType.getTextureId());

            matrices.push();
            matrices.translate(dx + 32, dy + 32, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);
            matrices.pop();

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(MathHelper.PI));
            matrices.translate(dx + 32, dy + 32, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);
            matrices.pop();

            matrices.pop();
        }

        private void renderSealLights(MatrixStack matrices, Random random, long time, float tickDelta) {
            refreshLights(random, time, tickDelta);
            float fillPercent = seal.getFillPercent();
            for (int i = 0, sealLightsSize = sealLights.size(); i < sealLightsSize; i++) {
                SealLight sealLight = sealLights.get(i);
                sealLight.render(matrices, sealType.getTextureLightId(), time, fillPercent, tickDelta, i * 0.01f);
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

        static {
            AURA_TEXTURES = IntStream.range(0, 23)
                    .mapToObj(i -> EIdentifier.of("textures/gui/seal_aura/seal_aura_" + i + ".png"))
                    .toArray(Identifier[]::new);
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
            matrices.translate(targetDx * percent, targetDy + percent, 0.1 + dz);
            matrices.multiply(new Quaternionf().rotateLocalZ(targetRoll * percent));
            matrices.translate(32, 32, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 64, 64, 64, 64);
            matrices.pop();
        }
    }
}
