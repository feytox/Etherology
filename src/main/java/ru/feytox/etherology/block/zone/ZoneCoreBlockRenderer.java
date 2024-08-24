package ru.feytox.etherology.block.zone;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.item.revelationView.RevelationViewItem;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

@RequiredArgsConstructor
public class ZoneCoreBlockRenderer implements BlockEntityRenderer<ZoneCoreBlockEntity> {

    @Environment(EnvType.CLIENT)
    private static boolean cachedCanSee = false;
    private static final Identifier TEXTURE = EIdentifier.of("textures/block/zone_core.png");
    private final BlockEntityRendererFactory.Context ctx;

    @Override
    public void render(ZoneCoreBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!canSeeZone(player)) return;

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        matrices.push();

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.translate(0.5f, 0.5f, 0f);
        matrices.scale(0.0625f, 0.0625f, 0.0625f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 16, 16, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        RenderSystem.disableCull();

        matrices.pop();
    }

    private static boolean canSeeZone(PlayerEntity player) {
        World world = player.getWorld();
        if (world == null || world.getTime() % 10 != 0) return cachedCanSee;

        if (RevelationViewItem.isEquipped(player)) {
            cachedCanSee = true;
            return true;
        }

        cachedCanSee = OculusItem.isInHands(player);
        return cachedCanSee;
    }
}
