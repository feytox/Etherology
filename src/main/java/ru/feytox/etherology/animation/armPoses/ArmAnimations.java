package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.item.OculusItem;

@UtilityClass
public class ArmAnimations {

    public static final ArmAnimation TEST_ANIMATION;
    public static final ArmAnimation TEST_ANIMATION_2;
    public static final ArmAnimation HAMMER_IDLE_LEFT;

    static {
        TEST_ANIMATION = ArmAnimation.Builder.create(5000, 0)
                .trigger(OculusItem::isUsingOculus)
                .keyframe(0, Ease.INCUBIC, ((bones, info) ->
                        bones.leftArm.pitch = -MathHelper.PI * 2))
                .build();
        TEST_ANIMATION_2 = ArmAnimation.Builder.create(2500, 1)
                .trigger(Entity::isSneaking)
                .animateArms(false)
                .keyframe(0, ((bones, info) ->
                        bones.leftArm.pitch = -MathHelper.PI * 2))
                .build();
        HAMMER_IDLE_LEFT = ArmAnimation.Builder.create(4100, 0)
                .trigger(OculusItem::isUsingOculus)
                .loop(true)
                .animateArms(false)
                .keyframe(0, Ease.INOUTQUAD, ((bones, info) -> {
                    bones.rightArm.yaw = MathHelper.PI * (1 / 4f);
                    bones.rightArm.roll = -MathHelper.PI * (1 / 2f);
                    bones.rightArm.pitch = -MathHelper.PI * (1 / 2f);
                    bones.rightArm.pivot.set(-5.0f, 2.0f, -2.0f);
                    bones.leftArm.roll = -MathHelper.PI * (1 / 4f);
                    bones.leftArm.yaw = MathHelper.PI * (5 / 18f);
                    bones.leftArm.pitch = -MathHelper.PI * (5 / 18f);
                    bones.leftArm.pivot.set(5.0f, 2.0f, -1.0f);}))
                .keyframe(50, Ease.INOUTQUAD, ((bones, info) -> {
                    bones.rightArm.yaw = MathHelper.PI * (1 / 4f);
                    bones.rightArm.roll = -MathHelper.PI * (8 / 15f);
                    bones.rightArm.pitch = -MathHelper.PI * (8 / 15f);
                    bones.rightArm.pivot.set(-5.0f, 2.0f, -2.0f);
                    bones.leftArm.roll = -MathHelper.PI * (1 / 4f);
                    bones.leftArm.yaw = MathHelper.PI * (13 / 45f);
                    bones.leftArm.pitch = -MathHelper.PI * (13 / 45f);
                    bones.leftArm.pivot.set(5.0f, 2.0f, -1.0f);}))
                .keyframe(2050, Ease.INOUTQUAD, ((bones, info) -> {
                    bones.rightArm.yaw = MathHelper.PI * (1 / 4f);
                    bones.rightArm.roll = -MathHelper.PI * (1 / 2f);
                    bones.rightArm.pitch = -MathHelper.PI * (1 / 2f);
                    bones.rightArm.pivot.set(-5.0f, 2.0f, -2.0f);
                    bones.leftArm.roll = -MathHelper.PI * (1 / 4f);
                    bones.leftArm.yaw = MathHelper.PI * (5 / 18f);
                    bones.leftArm.pitch = -MathHelper.PI * (5 / 18f);
                    bones.leftArm.pivot.set(5.0f, 2.0f, -1.0f);}))
                .build();
    }
}
