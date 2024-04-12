package ru.feytox.etherology.item;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import lombok.val;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;
import ru.feytox.etherology.gui.oculus.AspectComponent;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;
import ru.feytox.etherology.registry.item.ArmorItems;
import ru.feytox.etherology.util.feyapi.RenderUtils;

import java.util.Map;
import java.util.Set;

public class RevelationViewItem extends TrinketItem {

    @Nullable
    private static BlockPos lastTargetPos = null;
    private static float progress = 0.0f;
    @Nullable
    private static Vec3d cachedTargetPos = null;

    private static final int ROW_SIZE = 5;

    public RevelationViewItem() {
        super(new Settings().maxCount(1));
    }

    public static boolean isEquipped(LivingEntity entity) {
        val trinket = TrinketsApi.getTrinketComponent(entity).orElse(null);
        if (trinket == null) return false;
        return trinket.isEquipped(ArmorItems.REVELATION_VIEW);
    }

    public static void registerRendering() {
        WorldRenderEvents.END.register(RevelationViewItem::renderAspects);
    }

    private static void renderAspects(WorldRenderContext context) {
        // TODO: 07.04.2024 BIRCH LOG UNDO
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = context.world();
        HitResult hitResult = client.crosshairTarget;
        if (world == null || hitResult == null || !isEquipped(client.player)) return;

        if (isNewTarget(hitResult)) {
            progress = 0.0f;
            cachedTargetPos = null;
        }

        progress = MathHelper.lerp(0.1f*context.tickDelta(), progress, 1.0f);

        MatrixStack matrices = context.matrixStack();
        val aspects = getAspectsFromTarget(world, hitResult);
        Vec3d targetPos = getCachedTargetPos(hitResult);
        Vec3d offsetVec = getOffset(world, hitResult);
        if (aspects == null || aspects.isEmpty() || targetPos == null) return;

        matrices.push();

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        Vec3d renderPos = targetPos.subtract(cameraPos).add(offsetVec.multiply(progress));
        matrices.translate(renderPos.x, renderPos.y, renderPos.z);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        Set<Map.Entry<Aspect, Integer>> aspectsSet = aspects.getAspects().entrySet();
        int lastRowIndex = (aspectsSet.size() / ROW_SIZE) * ROW_SIZE;
        int lastRowSize = aspectsSet.size() % ROW_SIZE;
        lastRowSize = lastRowSize == 0 ? ROW_SIZE : lastRowSize;

        int i = 0;
        for (val entry : aspectsSet) {
            int row = i / ROW_SIZE;
            int col = i % ROW_SIZE;

            int r = i >= lastRowIndex ? lastRowSize : ROW_SIZE;
            float startOffset = r * 0.5f * 0.25f;

            renderAspect(matrices, entry.getKey(), -col * 0.25f + startOffset, row * 0.275f);
            renderCount(client, matrices, entry.getValue(), col, row, startOffset);
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
    private static Vec3d getCachedTargetPos(HitResult hitResult) {
        cachedTargetPos = cachedTargetPos != null ? cachedTargetPos : getPosFromTarget(hitResult);
        return cachedTargetPos;
    }

    @Nullable
    private static AspectContainer getAspectsFromTarget(ClientWorld world, HitResult hitResult) {
        return switch (hitResult.getType()) {
            case MISS, ENTITY -> null;
            case BLOCK -> {
                // TODO: 05.04.2024 replace with default thing
                if (!(hitResult instanceof BlockHitResult target)) yield null;
                yield AspectsLoader.getAspects(world.getBlockState(target.getBlockPos()).getBlock().asItem().getDefaultStack(), false).orElse(null);
            }
        };
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

    private static Vec3d getOffset(ClientWorld world, HitResult hitResult) {
        return switch (hitResult.getType()) {
            case MISS, ENTITY -> Vec3d.ZERO;
            case BLOCK -> {
                if (!(hitResult instanceof BlockHitResult target)) yield new Vec3d(0, 1, 0);
                Vec3i sideVec = target.getSide().getVector();
                Vec3d offset = Vec3d.of(sideVec);
                if (!world.getBlockState(target.getBlockPos().add(sideVec)).isAir()) offset = offset.multiply(0.5);
                yield offset;
            }
        };
    }
}
