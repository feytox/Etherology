package ru.feytox.etherology.mixin;

import net.minecraft.client.render.TexturedRenderLayers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    // TODO: 06.07.2024 check
//    @Inject(method = "createSignTextureId", at = @At("HEAD"), cancellable = true)
//    private static void onCreateSign(WoodType type, CallbackInfoReturnable<SpriteIdentifier> cir) {
//        if (type instanceof EtherSignType) {
//            cir.setReturnValue(new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, EIdentifier.of("entity/signs/" + type.getName())));
//        }
//    }
//
//    @Inject(method = "createHangingSignTextureId", at = @At("HEAD"), cancellable = true)
//    private static void onCreateHangSign(WoodType type, CallbackInfoReturnable<SpriteIdentifier> cir) {
//        if (type instanceof EtherSignType) {
//            cir.setReturnValue(new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, EIdentifier.of("entity/signs/hanging/" + type.getName())));
//        }
//    }
}
