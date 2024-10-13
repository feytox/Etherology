package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.util.misc.HideSurvivalBlockOutline;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final
    MinecraftClient client;

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void noBobbingWhenOculus(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (client.player == null) return;
        if (OculusItem.isInHands(client.player)) ci.cancel();
    }

    @ModifyReturnValue(method = "shouldRenderBlockOutline", at = @At("RETURN"))
    private boolean disableSealOutline(boolean original) {
        if (!original) return false;

        if (client.interactionManager == null) return true;
        if (client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) return true;

        var hitResult = client.crosshairTarget;
        if (hitResult == null || !hitResult.getType().equals(HitResult.Type.BLOCK) || client.world == null)
            return true;

        var blockPos = ((BlockHitResult)hitResult).getBlockPos();
        var block = client.world.getBlockState(blockPos).getBlock();
        if (!(block instanceof HideSurvivalBlockOutline))
            return true;

        return OculusItem.isInHands(client.player);
    }
}
