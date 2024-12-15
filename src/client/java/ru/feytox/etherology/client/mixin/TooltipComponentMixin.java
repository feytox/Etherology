package ru.feytox.etherology.client.mixin;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.client.gui.GlintTooltipComponent;
import ru.feytox.etherology.item.glints.GlintTooltipData;

@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {

    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",
            at = @At("HEAD"), cancellable = true)
    private static void onOf(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof GlintTooltipData glintData) {
            cir.setReturnValue(new GlintTooltipComponent(glintData));
        }
    }
}
