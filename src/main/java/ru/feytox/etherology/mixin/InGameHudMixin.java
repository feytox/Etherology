package ru.feytox.etherology.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.item.revelationView.RevelationViewRenderer;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.util.misc.EIdentifier;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void onDrawHeart(MatrixStack matrices, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (type == InGameHud.HeartType.CONTAINER || player == null) return;
        if (!EtherComponent.hasCurse(player)) return;

        int u = halfHeart ? 9 : 0;
        u += blinking ? 18 : 0;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, new EIdentifier("textures/hud/ether_hearts.png"));
        DrawableHelper.drawTexture(matrices, x, y, u, v, 9, 9, 256, 256);
        RenderSystem.setShaderTexture(0, new Identifier("minecraft", "textures/gui/icons.png"));
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    private void injectRevelationRendering(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        RevelationViewRenderer.renderOverlay(((InGameHud)(Object) this), tickDelta);
    }
}
