package ru.feytox.etherology.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.client.gui.ether.DevastatingHearts;
import ru.feytox.etherology.client.gui.ether.ExhaustionOverlay;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @WrapOperation(method = "drawHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud$HeartType;getTexture(ZZZ)Lnet/minecraft/util/Identifier;"))
    private Identifier injectEtherHearts(InGameHud.HeartType instance, boolean hardcore, boolean half, boolean blinking, Operation<Identifier> original) {
        Identifier changedTexture = DevastatingHearts.getDevastatingTexture(instance, hardcore, half, blinking);
        return changedTexture != null ? changedTexture : original.call(instance, hardcore, half, blinking);
    }

    @Inject(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    private void injectRevelationRendering(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ExhaustionOverlay.renderOverlay(context, ((InGameHud)(Object) this));
    }
}
