package ru.feytox.etherology.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.EnumUtils;
import ru.feytox.etherology.animation.armPoses.ArmAnimation;

import java.util.Arrays;
import java.util.Optional;

import static net.minecraft.util.math.MathHelper.PI;
import static ru.feytox.etherology.animation.armPoses.ArmAnimations.TEST_ANIMATION;
import static ru.feytox.etherology.animation.armPoses.ArmAnimations.TEST_ANIMATION_2;

@Getter
@RequiredArgsConstructor
public enum EArmPose {
    OCULUS(EArmPose.armAnimationPoser(TEST_ANIMATION, TEST_ANIMATION_2));

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

   private static ModelPoser armAnimationPoser(ArmAnimation... animations) {
        return (model, entity, isRightArm) ->
                Arrays.stream(animations).forEach(animation ->
                        ArmAnimation.testAndInject(animation, entity));
   }

    public interface ModelPoser {

        void accept(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm);
    }
}
