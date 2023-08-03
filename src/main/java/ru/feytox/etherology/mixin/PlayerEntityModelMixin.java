package ru.feytox.etherology.mixin;

import lombok.val;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.animation.armPoses.ArmAnimUtil;
import ru.feytox.etherology.animation.playerAnimation.PartsInfo;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;
import ru.feytox.etherology.util.feyapi.ExtendedKAP;

@Mixin(value = PlayerEntityModel.class, priority = 2001)
public class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {

    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;copyTransform(Lnet/minecraft/client/model/ModelPart;)V", ordinal = 0))
    private void applySneakingToEmote(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        applySneaking(livingEntity);
        ArmAnimUtil.tickCurrentAnimation(this, livingEntity);
    }

    @Unique
    private void applySneaking(T entity) {
        if (!this.sneaking) return;
        if (!(entity instanceof EtherologyPlayer player)) return;
        val animationContainer = player.etherology$getAnimation();
        val animation = animationContainer.getAnimation();
        if (!(animation instanceof ExtendedKAP etherAnimation)) {
            return;
        }

        PartsInfo partsInfo = etherAnimation.getAnim().getSneakingInfo();
        PartsInfo oldInfo = etherAnimation.getPreviousSneakingInfo();
        if (etherAnimation.getCurrentTick() == 0 && oldInfo != null) partsInfo = oldInfo;

        if (partsInfo.isBody()) this.body.pivotY += 3.2f;
        if (partsInfo.isHead()) {
            this.head.pivotY += 3.2f;
            this.hat.copyTransform(head);
        }
        if (partsInfo.isLeftArm()) this.leftArm.pivotY += 3.2f;
        if (partsInfo.isRightArm()) this.rightArm.pivotY += 3.2f;
        if (partsInfo.isLeftLeg()) {
            this.leftLeg.pivotY += 0.2f;
            this.leftLeg.pivotZ += 3.9f;
        }
        if (partsInfo.isRightLeg()) {
            this.rightLeg.pivotY += 0.2f;
            this.rightLeg.pivotZ += 3.9f;
        }
    }
}
