package ru.feytox.etherology.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.enums.EArmPose;

import java.util.Optional;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftArm;

    @Inject(method = "positionRightArm", at = @At("RETURN"))
    private void injectRightArmPoses(T entity, CallbackInfo ci) {
        etherology$injectArmPoses(entity, true);
    }

    @Inject(method = "positionLeftArm", at = @At("RETURN"))
    private void injectLeftArmPoses(T entity, CallbackInfo ci) {
        etherology$injectArmPoses(entity, false);
    }

    @Unique
    private void etherology$injectArmPoses(T entity, boolean isRightArm) {
        BipedEntityModel<?> model = ((BipedEntityModel<?>) (Object) this);
        BipedEntityModel.ArmPose checkPose = isRightArm ? model.rightArmPose : model.leftArmPose;
        Optional<EArmPose> poseOptional = EArmPose.getIfValid(checkPose.name());
        if (poseOptional.isEmpty()) return;

        poseOptional.get().getModelPoser().accept(model, entity, isRightArm);
    }
}
