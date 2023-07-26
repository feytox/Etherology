package ru.feytox.etherology.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.EnumUtils;

import java.util.Optional;

import static net.minecraft.util.math.MathHelper.PI;

@RequiredArgsConstructor
public enum EArmPose {
    OCULUS(EArmPose::oculusPoser);

    @Getter
    private final ModelPoser modelPoser;

    public BipedEntityModel.ArmPose getArmPose() {
        return BipedEntityModel.ArmPose.valueOf(name());
    }

    public UseAction getUseAction() {
        return UseAction.valueOf(name());
    }

    public static Optional<EArmPose> getIfValid(String name) {
        return Optional.ofNullable(EnumUtils.getEnum(EArmPose.class, name));
    }

   private static void oculusPoser(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
        val arm = isRightArm ? model.rightArm : model.leftArm;
        val value = model.head.pitch - PI * (1 / 2.4f) - (entity.isInSneakingPose() ? 0.2617994F : 0.0F);
        arm.pitch = MathHelper.clamp(value, -2.2F, -0.75f);
        arm.yaw = model.head.yaw;
   }

    public interface ModelPoser {

        void accept(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm);
    }
}
