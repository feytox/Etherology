package ru.feytox.etherology.mixin;

import com.github.crimsondawn45.fabricshieldlib.initializers.FabricShieldLib;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.Etherology;

@Mixin(value = FabricShieldLib.class, remap = false)
public class FabricShieldLibMixin {

    @ModifyExpressionValue(method = "onInitialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/FabricLoader;isDevelopmentEnvironment()Z"))
    private boolean forceNonDevEnvironment(boolean original) {
        if (original) Etherology.ELOGGER.info("Developer Mode was force disabled for Fabric Shield Lib.");
        return false;
    }
}
