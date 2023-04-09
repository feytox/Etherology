package ru.feytox.etherology.mixin;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.DecoBlocks;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@Mixin(EntityModelLayers.class)
public class EntityModelLayersMixin {

    @Inject(method = "createSign", at = @At("HEAD"), cancellable = true)
    private static void onCreateSign(SignType type, CallbackInfoReturnable<EntityModelLayer> cir) {
        if (type.equals(DecoBlocks.PEACH_SIGN_TYPE)) {
            cir.setReturnValue(new EntityModelLayer(new EIdentifier("sign/" + type.getName()), "main"));
        }
    }
}
