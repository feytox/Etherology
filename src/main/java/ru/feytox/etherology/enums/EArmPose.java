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
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.registry.util.EtherologyComponents;

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
    TUNING_MACE_ETHEROLOGY(EArmPose::macePoser);

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

    private static void staffPoser(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
        ItemStack staffStack = entity.getActiveItem();
        val arm = isRightArm ? model.rightArm : model.leftArm;
        if (!(staffStack.getItem() instanceof StaffItem)) {
            // staff in arm, but not using = vanilla
            arm.pitch = arm.pitch * 0.5F - 0.31415927F;
            return;
        }

        val lensData = EtherologyComponents.LENS.get(staffStack);
        if (lensData.isEmpty()) return;
        val lensMode = lensData.getLensMode();

        switch (lensMode) {
            case STREAM -> {
                float angle = model.head.pitch - PI * (1 / 2.4f) - (entity.isInSneakingPose() ? 0.2617994F : 0.0F);
                arm.pitch = MathHelper.clamp(angle, -2.2F, -0.75f);
                arm.yaw = model.head.yaw;
            }
            case CHARGE -> arm.pitch = PI;
        }
    }

    private static void macePoser(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
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
