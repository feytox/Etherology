package ru.feytox.etherology.item.revelationView;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.oculus.AspectComponent;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;
import ru.feytox.etherology.magic.aspects.RevelationAspectProvider;
import ru.feytox.etherology.magic.corruption.CorruptionComponent;
import ru.feytox.etherology.mixin.InGameHudAccessor;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;

@UtilityClass
public class RevelationViewRenderer {

    // constants
    private static final int ROW_SIZE = 5;
    private static final int DATA_TICK_RATE = 5;
    private static final float MIN_CORRUPTION_OVERLAY = 32.0f;
    private static final Identifier CORRUPTION_TEXTURE = new EIdentifier("textures/misc/corruption_outline.png");

    // overlay cache
    private static float targetOpacity = 0.0f;
    private static float currentOpacity = 0.0f;

    // aspects cache
    private static @Nullable BlockPos lastTargetPos = null;
    private static @Nullable List<Pair<Aspect, Integer>> aspects = null;
    private static @Nullable Vec3d targetPos = null;
    private static @Nullable Vec3d offsetVec = null;
    private static float progress = 0.0f;

    public static void tickData(World world, PlayerEntity player) {
        if (world.getTime() % DATA_TICK_RATE != 0) return;
        if (!RevelationViewItem.isEquipped(player)) {
            targetOpacity = 0.0f;
            return;
        }

        val corruptionLevel = getCorruptionLevel(world, player);
        if (corruptionLevel == null || corruptionLevel == 0.0f) targetOpacity = 0.0f;
        else targetOpacity = (corruptionLevel - MIN_CORRUPTION_OVERLAY) / (CorruptionComponent.MAX_CHUNK_CORRUPTION - MIN_CORRUPTION_OVERLAY);

        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitResult = client.crosshairTarget;
        if (hitResult == null) {
            aspects = null;
            targetPos = null;
            offsetVec = null;
            return;
        }

        refreshData(world, player, hitResult);
    }

    private static void refreshData(World world, PlayerEntity player, HitResult hitResult) {
        aspects = RevelationAspectProvider.getSortedAspects(world, hitResult);
        targetPos = getPosFromTarget(hitResult);
        offsetVec = getOffset(world, player, hitResult);
    }

    public static void registerRendering() {
        WorldRenderEvents.END.register(RevelationViewRenderer::renderAspects);
    }

    private static void renderAspects(WorldRenderContext context) {
        if (aspects == null || aspects.isEmpty() || targetPos == null || offsetVec == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = context.world();
        HitResult hitResult = client.crosshairTarget;
        if (world == null || hitResult == null || !RevelationViewItem.isEquipped(client.player)) return;

        if (isNewTarget(hitResult)) {
            progress = 0.0f;
            refreshData(world, client.player, hitResult);
        }

        progress = MathHelper.lerp(0.1f*context.tickDelta(), progress, 1.0f);
        MatrixStack matrices = context.matrixStack();
        if (aspects == null || aspects.isEmpty() || targetPos == null || offsetVec == null) return;

        matrices.push();

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        Vec3d renderPos = targetPos.subtract(cameraPos).add(offsetVec.multiply(progress));
        matrices.translate(renderPos.x, renderPos.y, renderPos.z);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        int lastRowIndex = (aspects.size() / ROW_SIZE) * ROW_SIZE;
        int lastRowSize = aspects.size() % ROW_SIZE;
        lastRowSize = lastRowSize == 0 ? ROW_SIZE : lastRowSize;

        int i = 0;
        for (Pair<Aspect, Integer> pair : aspects) {
            int row = i / ROW_SIZE;
            int col = i % ROW_SIZE;

            int r = i >= lastRowIndex ? lastRowSize : ROW_SIZE;
            float startOffset = r * 0.5f * 0.25f;

            renderAspect(matrices, pair.key(), -col * 0.25f + startOffset, row * 0.275f);
            renderCount(client, matrices, pair.value(), col, row, startOffset);
            i++;
        }

        matrices.pop();
    }

    private static void renderAspect(MatrixStack matrices, Aspect aspect, float dx, float dy) {
        matrices.push();
        RenderSystem.setShaderTexture(0, AspectComponent.TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        matrices.scale(progress, progress, progress);
        matrices.translate(dx, dy, 0);
        RenderUtils.renderTexture(matrices, 0, 0, 0, aspect.getTextureMinX(), aspect.getTextureMinY(), 0.25f, 0.25f, 32, 32, EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
        matrices.pop();
    }

    private static void renderCount(MinecraftClient client, MatrixStack matrices, Integer count, int col, int row, float startOffset) {
        matrices.push();
        matrices.scale(progress, progress, progress);
        matrices.translate(-col * 0.25f + startOffset - 0.18f, row * 0.275f - 0.18f, -0.0001);
        matrices.scale(-0.008F, -0.008F, 0.025F);
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        client.textRenderer.draw(count.toString(), 0, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), immediate, true, 0, 15728880);
        immediate.draw();
        matrices.pop();
    }

    @Nullable
    private static Vec3d getPosFromTarget(HitResult hitResult) {
        return switch (hitResult.getType()) {
            case MISS, ENTITY -> null;
            case BLOCK -> {
                if (!(hitResult instanceof BlockHitResult target)) yield null;
                yield target.getBlockPos().toCenterPos();
            }
        };
    }

    private static boolean isNewTarget(HitResult hitResult) {
        return switch (hitResult.getType()) {
            case MISS, ENTITY -> true;
            case BLOCK -> {
                if (!(hitResult instanceof BlockHitResult target)) yield true;
                boolean result = !target.getBlockPos().equals(lastTargetPos);
                lastTargetPos = target.getBlockPos();
                yield result;
            }
        };
    }

    private static Vec3d getOffset(World world, PlayerEntity player, HitResult hitResult) {
        return switch (hitResult.getType()) {
            case MISS, ENTITY -> Vec3d.ZERO;
            case BLOCK -> {
                if (!(hitResult instanceof BlockHitResult target)) yield new Vec3d(0, 1, 0);
                if (player.getPitch() > 0.0f && world.getBlockState(target.getBlockPos().up()).isAir()) {
                    yield new Vec3d(0, 1, 0);
                }

                Vec3i sideVec = target.getSide().getVector();
                Vec3d offset = Vec3d.of(sideVec);
                if (!world.getBlockState(target.getBlockPos().add(sideVec)).isAir()) offset = offset.multiply(0.5);
                yield offset;
            }
        };
    }

    public static void renderOverlay(InGameHud hud, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || !RevelationViewItem.isEquipped(player)) targetOpacity = 0.0f;

        currentOpacity = MathHelper.lerp(0.1f*tickDelta, currentOpacity, targetOpacity);
        if (currentOpacity <= 0.00001f) return;
        ((InGameHudAccessor) hud).callRenderOverlay(CORRUPTION_TEXTURE, currentOpacity);
    }

    @Nullable
    private static Float getCorruptionLevel(World world, PlayerEntity player) {
        if (player == null || world == null || !RevelationViewItem.isEquipped(player)) return null;

        val component = world.getChunk(player.getBlockPos()).getComponent(EtherologyComponents.CORRUPTION);
        val corruption = component.getCorruption();
        return corruption == null ? null : corruption.getCorruptionValue();
    }
}
