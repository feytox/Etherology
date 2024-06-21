package ru.feytox.etherology.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.registry.block.DecoBlocks;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    private void injectForestLanternSpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (state.isOf(DecoBlocks.FOREST_LANTERN)) cir.setReturnValue(15.0f);
    }
}
