package ru.feytox.etherology.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Deprecated
@Mixin(value = PlayerEntityModel.class, priority = 2001)
public class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {

//    @Unique
//    private PartsInfo cachedModelState = null;
//
    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }
//
//    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", shift = At.Shift.AFTER))
//    private void cacheDefaultState(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
//        cachedModelState = PartsInfo.of(this);
//    }
//
//    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;copyTransform(Lnet/minecraft/client/model/ModelPart;)V", ordinal = 0))
//    private void applySneakingToEmote(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
//        applySneaking();
//    }
//
//    @Unique
//    private void applySneaking() {
//        if (!this.sneaking) return;
//        if (cachedModelState == null) return;
//
//        if (cachedModelState.headY() != this.head.pivotY) {
//            this.head.pivotY = 4.2f;
//            this.hat.copyTransform(this.head);
//        }
//        if (cachedModelState.bodyY() != this.body.pivotY) this.body.pivotY = 3.2f;
//        if (cachedModelState.rightArmY() != this.rightArm.pivotY) this.rightArm.pivotY = 5.2f;
//        if (cachedModelState.leftArmY() != this.leftArm.pivotY) this.leftArm.pivotY = 5.2f;
//        if (cachedModelState.rightLegY() != this.rightLeg.pivotY) this.rightLeg.pivotY = 12.2f;
//        if (cachedModelState.leftLegY() != this.leftLeg.pivotY) this.leftLeg.pivotY = 12.2f;
//        if (cachedModelState.rightLegZ() != this.rightLeg.pivotZ) this.rightLeg.pivotZ = 4.0f;
//        if (cachedModelState.leftLegZ() != this.leftLeg.pivotZ) this.leftLeg.pivotZ = 4.0f;
//    }
}
