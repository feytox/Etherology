package ru.feytox.etherology.client.mixin;

import com.github.crimsondawn45.fabricshieldlib.initializers.FabricShieldLibClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.Etherology;

@Mixin(value = FabricShieldLibClient.class, remap = false)
public class FabricShieldLibClientMixin {

    @ModifyExpressionValue(method = "onInitializeClient", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/FabricLoader;isDevelopmentEnvironment()Z"))
    private boolean forceNonDevEnvironment(boolean original) {
        if (original) Etherology.ELOGGER.info("Developer Mode was force disabled for Fabric Shield Lib.");
        return false;
    }
}
