package ru.feytox.etherology.mixin;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.HangingSignEditScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.blocks.signs.EtherSignBlockEntity;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@Mixin(HangingSignEditScreen.class)
public class HangingSignEditScreenMixin {

    @Mutable
    @Final
    @Shadow
    private Identifier texture;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(SignBlockEntity signBlockEntity, boolean bl, CallbackInfo ci) {
        if (signBlockEntity instanceof EtherSignBlockEntity) {
            SignType signType = AbstractSignBlock.getSignType(signBlockEntity.getCachedState().getBlock());
            texture = new EIdentifier("textures/gui/hanging_signs/" + signType.getName() + ".png");
        }
    }
}
