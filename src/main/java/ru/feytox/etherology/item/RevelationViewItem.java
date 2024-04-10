package ru.feytox.etherology.item;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import lombok.val;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
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
        HitResult target = client.crosshairTarget;
        if (world == null || target == null || !isEquipped(client.player)) return;

        if (isNewTarget(target)) progress = 0.0f;
        progress = MathHelper.lerp(0.1f*context.tickDelta(), progress, 1.0f);

        MatrixStack matrices = context.matrixStack();
        AspectContainer aspects = getAspectsFromTarget(world, target);
        Vec3d targetPos = getPosFromTarget(target);
        if (aspects == null || aspects.isEmpty() || targetPos == null) return;

        matrices.push();

        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();
        Vec3d offsetVec = targetPos.add(0, 1.0, 0).subtract(cameraPos);
        matrices.translate(offsetVec.x, offsetVec.y, offsetVec.z);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

        int rowSize = 5; // TODO: 07.04.2024 move to constants
        Set<Map.Entry<Aspect, Integer>> aspectsSet = aspects.getAspects().entrySet();
        int lastRowIndex = (aspectsSet.size() / rowSize) * rowSize;
        int lastRowSize = aspectsSet.size() % rowSize;
        lastRowSize = lastRowSize == 0 ? rowSize : lastRowSize;

        int i = 0;
        for (val entry : aspectsSet) {
            int row = i / rowSize;
            int col = i % rowSize;

            int r = i >= lastRowIndex ? lastRowSize : rowSize;
            float startOffset = r * 0.5f * 0.25f;

            renderAspect(matrices, entry.getKey(), -col * 0.25f + startOffset, row * 0.275f);
            renderCount(entry.getValue(), matrices, col, startOffset, row, client);
            i++;
        }

        matrices.pop();
    }

    private static void renderAspect(MatrixStack matrices, Aspect aspect, float dx, float dy) {
        matrices.push();
        RenderSystem.setShaderTexture(0, AspectComponent.TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        matrices.scale(progress, progress, progress);
        matrices.translate(dx, dy, 0);
        RenderUtils.renderTexture(matrices, 0, 0, 0, aspect.getTextureMinX(), aspect.getTextureMinY(), 0.25f, 0.25f, 32, 32, EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
        matrices.pop();
    }

    private static void renderCount(Integer count, MatrixStack matrices, int col, float startOffset, int row, MinecraftClient client) {
        matrices.push();
        matrices.scale(progress, progress, progress);
        matrices.translate(-col * 0.25f + startOffset - 0.18f, row * 0.275f - 0.18f, -0.0001);
        matrices.scale(-0.008F, -0.008F, 0.025F);
        client.textRenderer.draw(matrices, count.toString(), 0, 0, 0xFFFFFF);
        matrices.pop();
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
}
