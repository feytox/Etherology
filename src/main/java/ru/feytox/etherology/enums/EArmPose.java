package ru.feytox.etherology.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.EnumUtils;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;

import java.util.Optional;

import static net.minecraft.util.math.MathHelper.PI;
import static net.minecraft.util.math.MathHelper.RADIANS_PER_DEGREE;

/**
 * @see ru.feytox.etherology.mixin.EarlyRisers
 */
@Getter
@RequiredArgsConstructor
public enum EArmPose {
    OCULUS_ETHEROLOGY(EArmPose::oculusPoser),
    STAFF_ETHEROLOGY(EArmPose::staffPoser),
    TWOHANDHELD_ETHEROLOGY(EArmPose::twoPoser);

    private final ModelPoser modelPoser;

    public BipedEntityModel.ArmPose getArmPose() {
        return BipedEntityModel.ArmPose.valueOf(name());
    }

    public UseAction getUseAction() {
        return UseAction.valueOf(name());
    }

    public static boolean isValid(String name) {
        return EnumUtils.isValidEnumIgnoreCase(EArmPose.class, name);
    }

    public static Optional<EArmPose> getIfValid(String name) {
        return Optional.ofNullable(EnumUtils.getEnum(EArmPose.class, name));
    }

    public static <T extends LivingEntity> boolean injectArmPoses(BipedEntityModel<?> model, T entity, boolean isRightArm) {
        BipedEntityModel.ArmPose checkPose = isRightArm ? model.rightArmPose : model.leftArmPose;
        Optional<EArmPose> poseOptional = EArmPose.getIfValid(checkPose.name());
        if (poseOptional.isEmpty()) return false;

        poseOptional.get().getModelPoser().accept(model, entity, isRightArm);
        return true;
    }

    // TODO: 31.05.2024 Add additional code when required
    public static UseAction injectUseActions(UseAction useAction) {
        return isValid(useAction.name()) ? UseAction.NONE : useAction;
    }

    // TODO: 27.04.2024 consider move posers to their item classes
    private static void oculusPoser(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
        val arm = isRightArm ? model.rightArm : model.leftArm;
        val value = model.head.pitch - PI * (1 / 2.4f) - (entity.isInSneakingPose() ? 0.2617994F : 0.0F);
        arm.pitch = MathHelper.clamp(value, -2.2F, -0.75f);
        arm.yaw = model.head.yaw;
    }

    private static void staffPoser(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
        ItemStack staffStack = entity.getActiveItem();
        val arm = isRightArm ? model.rightArm : model.leftArm;
        if (!(staffStack.getItem() instanceof StaffItem)) {
            // staff in arm, but not using = vanilla
            arm.pitch = arm.pitch * 0.5F - 0.31415927F;
            return;
        }

        val lensData = LensItem.getStaffLensComponent(staffStack);
        if (lensData == null) return;

        switch (lensData.mode()) {
            case STREAM -> {
                float angle = model.head.pitch - PI * (1 / 2.4f) - (entity.isInSneakingPose() ? 0.2617994F : 0.0F);
                arm.pitch = MathHelper.clamp(angle, -2.2F, -0.75f);
                arm.yaw = model.head.yaw;
            }
            case CHARGE -> arm.pitch = PI;
        }
    }

    private static void twoPoser(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
        val mainArm = isRightArm ? model.rightArm : model.leftArm;
        val otherArm = isRightArm ? model.leftArm : model.rightArm;
        int d = isRightArm ? 1 : -1;

        mainArm.pitch = -55 * RADIANS_PER_DEGREE;
        mainArm.yaw = -15 * RADIANS_PER_DEGREE * d;
        mainArm.roll = -32 * RADIANS_PER_DEGREE * d;

        otherArm.pitch = -35 * RADIANS_PER_DEGREE;
        otherArm.yaw = 43 * RADIANS_PER_DEGREE * d;
        otherArm.roll = 5 * RADIANS_PER_DEGREE * d;
    }

    public interface ModelPoser {

        void accept(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm);
    }
}
