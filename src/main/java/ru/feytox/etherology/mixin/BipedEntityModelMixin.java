package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.armPoses.ArmAnimUtil;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import java.util.Optional;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow public boolean sneaking;

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

    @WrapWithCondition(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;animateArms(Lnet/minecraft/entity/LivingEntity;F)V"))
    private boolean injectArmAnimations(BipedEntityModel<T> instance, T entity, float h) {
        if (!(entity instanceof EtherologyPlayer)) return true;
        ArmAnimUtil.tickCurrentAnimation(instance, entity);
        return ArmAnimUtil.shouldAnimateArms(entity);
    }

    @Unique
    private void etherology$injectArmPoses(T entity, boolean isRightArm) {
        BipedEntityModel<?> model = ((BipedEntityModel<?>) (Object) this);
        BipedEntityModel.ArmPose checkPose = isRightArm ? model.rightArmPose : model.leftArmPose;
        Optional<EArmPose> poseOptional = EArmPose.getIfValid(checkPose.name());
        if (poseOptional.isEmpty()) return;

        poseOptional.get().getModelPoser().accept(model, entity, isRightArm);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;sneaking:Z", opcode = Opcodes.GETFIELD))
    private void cancelSneakingAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!sneaking) return;
        if (!ArmAnimUtil.shouldAnimateArms(livingEntity)) {
            rightArm.pitch -= 0.4f;
            leftArm.pitch -= 0.4f;
        }
    }
}
