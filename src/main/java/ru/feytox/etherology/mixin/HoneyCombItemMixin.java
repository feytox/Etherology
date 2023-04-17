package ru.feytox.etherology.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.BlocksRegistry;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneyCombItemMixin {

    @Inject(method = "getWaxedState", at = @At("HEAD"), cancellable = true)
    private static void onGetWaxedState(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (state.isOf(BlocksRegistry.JUG)) {
            cir.setReturnValue(Optional.of(BlocksRegistry.SEALED_JUG.getStateWithProperties(state)));
        }
    }
}
