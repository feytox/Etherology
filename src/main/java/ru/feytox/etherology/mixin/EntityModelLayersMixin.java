package ru.feytox.etherology.mixin;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityModelLayers.class)
public class EntityModelLayersMixin {
    // TODO: 06.07.2024 check
//    @Inject(method = "createSign", at = @At("HEAD"), cancellable = true)
//    private static void onCreateSign(WoodType type, CallbackInfoReturnable<EntityModelLayer> cir) {
//        if (type instanceof EtherSignType) {
//            cir.setReturnValue(new EntityModelLayer(EIdentifier.of("sign/" + type.getName()), "main"));
//        }
//    }
//
//    @Inject(method = "createHangingSign", at = @At("HEAD"), cancellable = true)
//    private static void onCreateHangSign(WoodType type, CallbackInfoReturnable<EntityModelLayer> cir) {
//        if (type instanceof EtherSignType) {
//            cir.setReturnValue(new EntityModelLayer(EIdentifier.of("hanging_sign/" + type.getName()), "main"));
//        }
//    }
}
