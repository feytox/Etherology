package ru.feytox.etherology.mixin;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.block.signs.EtherSignType;
import ru.feytox.etherology.util.misc.EIdentifier;

import static net.minecraft.client.render.TexturedRenderLayers.SIGNS_ATLAS_TEXTURE;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    @Inject(method = "createSignTextureId", at = @At("HEAD"), cancellable = true)
    private static void onCreateSign(SignType type, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (type instanceof EtherSignType) {
            cir.setReturnValue(new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new EIdentifier("entity/signs/" + type.getName())));
        }
    }

    @Inject(method = "createHangingSignTextureId", at = @At("HEAD"), cancellable = true)
    private static void onCreateHangSign(SignType type, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (type instanceof EtherSignType) {
            cir.setReturnValue(new SpriteIdentifier(SIGNS_ATLAS_TEXTURE, new EIdentifier("entity/signs/hanging/" + type.getName())));
        }
    }
}
