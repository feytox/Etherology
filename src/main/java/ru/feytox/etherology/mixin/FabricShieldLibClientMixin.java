package ru.feytox.etherology.mixin;

import com.github.crimsondawn45.fabricshieldlib.initializers.FabricShieldLibClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static ru.feytox.etherology.Etherology.ELOGGER;

@Mixin(value = FabricShieldLibClient.class, remap = false)
public class FabricShieldLibClientMixin {

    @SuppressWarnings("SameReturnValue")
    @ModifyExpressionValue(method = "onInitializeClient", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/FabricLoader;isDevelopmentEnvironment()Z"))
    private boolean disableDevEnv(boolean original) {
        if (!original) return false;
        ELOGGER.warn("Developer mode has been disabled for Fabric Shield Lib to avoid crash. The warning and restriction will be removed after the fix either by FSL or by Etherology.");
        return false;
    }
}
