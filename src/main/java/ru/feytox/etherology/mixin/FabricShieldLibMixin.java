package ru.feytox.etherology.mixin;

import com.github.crimsondawn45.fabricshieldlib.initializers.FabricShieldLib;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static ru.feytox.etherology.Etherology.ELOGGER;

@Mixin(value = FabricShieldLib.class, remap = false)
public class FabricShieldLibMixin {

    @SuppressWarnings("SameReturnValue")
    @ModifyExpressionValue(method = "onInitialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/FabricLoader;isDevelopmentEnvironment()Z"))
    private boolean disableDevEnv(boolean original) {
        // TODO: #upd
        if (!original) return false;
        ELOGGER.warn("Developer mode has been disabled for Fabric Shield Lib to avoid crash. The warning and restriction will be removed after the fix either by FSL or by Etherology.");
        return false;
    }
}
