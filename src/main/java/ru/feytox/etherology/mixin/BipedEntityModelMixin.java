package ru.feytox.etherology.mixin;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.enums.EArmPose;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {

    @Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
    private void injectRightArmPoses(T entity, CallbackInfo ci) {
        boolean result = EArmPose.injectArmPoses(((BipedEntityModel<?>) (Object) this), entity, true);
        if (result) ci.cancel();
    }

    @Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
    private void injectLeftArmPoses(T entity, CallbackInfo ci) {
        boolean result = EArmPose.injectArmPoses(((BipedEntityModel<?>) (Object) this), entity, false);
        if (result) ci.cancel();
    }
}
