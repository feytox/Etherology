package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import lombok.val;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.armPoses.ArmAnimation;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import java.util.Optional;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {

    @Inject(method = "positionRightArm", at = @At("RETURN"))
    private void injectRightArmPoses(T entity, CallbackInfo ci) {
        etherology$injectArmPoses(entity, true);
    }

    @Inject(method = "positionLeftArm", at = @At("RETURN"))
    private void injectLeftArmPoses(T entity, CallbackInfo ci) {
        etherology$injectArmPoses(entity, false);
    }

    @WrapWithCondition(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private boolean injectArmAnimations(BipedEntityModel<T> instance, T entity, float h) {
        if (!(entity instanceof EtherologyPlayer player)) return true;
        ArmAnimation.tickCurrentAnimation(instance, entity);

        val animation = player.etherology$getCurrentArmAnimation();
        return animation == null || animation.getAnimation().isAnimateArms();
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
