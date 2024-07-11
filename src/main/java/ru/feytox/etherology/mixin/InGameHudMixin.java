package ru.feytox.etherology.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.util.misc.EIdentifier;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void onDrawHeart(DrawContext context, InGameHud.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (type.equals(InGameHud.HeartType.CONTAINER) || player == null) return;
        if (type.equals(InGameHud.HeartType.ABSORBING)) return;
        if (!EtherComponent.hasCurse(player)) return;

        int u = half ? 9 : 0;
        u += blinking ? 18 : 0;
        context.drawTexture(EIdentifier.of("textures/hud/ether_hearts.png"), x, y, );
        DrawContext.drawTexture(matrices, x, y, u, v, 9, 9, 256, 256);
        ci.cancel();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    private void injectRevelationRendering(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        EtherComponent.renderOverlay(((InGameHud)(Object) this));
    }
}
