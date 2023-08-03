package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;
import ru.feytox.etherology.item.OculusItem;

@UtilityClass
public class ArmAnimations {

    public static final ArmAnimation HAMMER_IDLE_LEFT;
    public static final ArmAnimation HAMMER_HIT_LEFT;

    public static final ArmAnimation DEFAULT_ANIMATION;

    static {
        DEFAULT_ANIMATION = ArmAnimation.Builder.create(150, 100)
                .keyframe(0, Ease.INOUTQUAD, (bones, info) -> {})
                .transitionAfter(false)
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
                .keyframe(100, Ease.INOUTQUAD, ((bones, info) -> {
                    bones.rightArm.yaw = MathHelper.PI * (1 / 4f);
                    bones.rightArm.roll = -MathHelper.PI * (8 / 15f);
                    bones.rightArm.pitch = -MathHelper.PI * (8 / 15f);
                    bones.rightArm.pivot.set(-5.0f, 2.0f, -2.0f);
                    bones.leftArm.roll = -MathHelper.PI * (1 / 4f);
                    bones.leftArm.yaw = MathHelper.PI * (13 / 45f);
                    bones.leftArm.pitch = -MathHelper.PI * (13 / 45f);
                    bones.leftArm.pivot.set(5.0f, 2.0f, -1.0f);}))
                .keyframe(2100, Ease.INOUTQUAD, ((bones, info) -> {
                    bones.rightArm.yaw = MathHelper.PI * (1 / 4f);
                    bones.rightArm.roll = -MathHelper.PI * (1 / 2f);
                    bones.rightArm.pitch = -MathHelper.PI * (1 / 2f);
                    bones.rightArm.pivot.set(-5.0f, 2.0f, -2.0f);
                    bones.leftArm.roll = -MathHelper.PI * (1 / 4f);
                    bones.leftArm.yaw = MathHelper.PI * (5 / 18f);
                    bones.leftArm.pitch = -MathHelper.PI * (5 / 18f);
                    bones.leftArm.pivot.set(5.0f, 2.0f, -1.0f);}))
                .build();

        HAMMER_HIT_LEFT = ArmAnimation.Builder.create(600, 1)
                .animateArms(false)
                .loop(true)
                .trigger(OculusItem::isUsingOculus)
                .keyframe(0, Ease.INOUTQUAD, (bones, info) -> {
                    bones.head.pivot.setX(-0.0f);
                    bones.head.pivot.setY(-0.0f);
                    bones.head.pivot.setZ(-0.0f);
                    bones.head.pitch = 0.5235987901687622f;
                    bones.head.roll = 1.1102231569740545e-16f;
                    bones.head.yaw = -2.974833973805286e-17f;
                    bones.body.yaw = 7.565994415534705e-18f;
                    bones.body.roll = -5.746937229130156e-17f;
                    bones.body.pitch = 0.2617993950843811f;
                    bones.body.pivot.setX(0.0f);
                    bones.body.pivot.setY(0.0625f);
                    bones.body.pivot.setZ(0.03125f);
                    bones.rightArm.yaw = 0.2617993950843811f;
                    bones.rightArm.roll = -2.5179293142819437e-16f;
                    bones.rightArm.pivot.setZ(1.0f);
                    bones.rightArm.pivot.setY(1.0f);
                    bones.rightArm.pivot.setX(-3.0f);
                    bones.rightArm.pitch = -4.188790321350098f;
                    bones.leftArm.roll = 4.440892098500626e-16f;
                    bones.leftArm.yaw = -0.2617993950843811f;
                    bones.leftArm.pitch = -4.188790321350098f;
                    bones.leftArm.pivot.setZ(1.0f);
                    bones.leftArm.pivot.setY(0.9999999403953552f);
                    bones.leftArm.pivot.setX(3.0f);
                    bones.rightLeg.pivot.setX(-1.9f);
                    bones.rightLeg.pivot.setY(-4.700000190734864f);
                    bones.rightLeg.pivot.setZ(9.200000047683716f);
                    bones.rightLeg.pitch = 0.4363323152065277f;
                    bones.rightLeg.roll = 9.384010016111926e-17f;
                    bones.rightLeg.yaw = -2.080382401628016e-17f;
                    bones.leftLeg.pivot.setX(1.9f);
                    bones.leftLeg.pivot.setY(-0.1f);
                    bones.leftLeg.pivot.setZ(12.479999989271164f);
                    bones.leftLeg.pitch = 0.1745329052209854f;
                    bones.leftLeg.roll = 3.8557634590730296e-17f;
                    bones.leftLeg.yaw = -3.373320292968827e-18f;
                })
                .keyframe(50, Ease.INOUTQUAD, (bones, info) -> {
                    bones.head.pivot.setX(-0.0f);
                    bones.head.pivot.setY(-0.0f);
                    bones.head.pivot.setZ(-0.0f);
                    bones.head.pitch = 0.2617993950843811f;
                    bones.head.roll = 9.912351449173226e-17f;
                    bones.head.yaw = -5.576602873904257e-17f;
                    bones.rightLeg.pivot.setX(-1.9f);
                    bones.rightLeg.pivot.setY(-0.700000011920929f);
                    bones.rightLeg.pivot.setZ(11.0f);
                    bones.rightLeg.pitch = -1.186177372932434f;
                    bones.rightLeg.roll = 7.501153712550143e-17f;
                    bones.rightLeg.yaw = -8.482177345892723e-17f;
                })
                .keyframe(150, Ease.INOUTQUAD, (bones, info) -> {
                    bones.body.yaw = 1.6457521939883995e-16f;
                    bones.body.roll = 2.1447860223769048e-16f;
                    bones.body.pitch = -1.3089969158172607f;
                    bones.body.pivot.setX(0.0f);
                    bones.body.pivot.setY(-0.25f);
                    bones.body.pivot.setZ(-0.03125f);
                    bones.rightArm.yaw = -0.2617993950843811f;
                    bones.rightArm.roll = -1.647086535578121e-16f;
                    bones.rightArm.pivot.setZ(2.0f);
                    bones.rightArm.pivot.setY(-1.0f);
                    bones.rightArm.pivot.setX(-3.0f);
                    bones.rightArm.pitch = -1.3089969158172607f;
                    bones.leftArm.roll = -0.0f;
                    bones.leftArm.yaw = 0.2617993950843811f;
                    bones.leftArm.pitch = -1.3089969158172607f;
                    bones.leftArm.pivot.setZ(2.0f);
                    bones.leftArm.pivot.setY(-1.0f);
                    bones.leftArm.pivot.setX(3.0f);
                    bones.rightLeg.pivot.setX(-1.9f);
                    bones.rightLeg.pivot.setY(-0.3000000059604645f);
                    bones.rightLeg.pivot.setZ(9.400000095367432f);
                    bones.rightLeg.pitch = -1.3089969158172607f;
                    bones.rightLeg.roll = -2.1447858900280068e-16f;
                    bones.rightLeg.yaw = -1.6457521939883995e-16f;
                    bones.leftLeg.pivot.setX(1.9f);
                    bones.leftLeg.pivot.setY(0.37999998927116396f);
                    bones.leftLeg.pivot.setZ(12.0f);
                    bones.leftLeg.pitch = -0.9599310755729675f;
                    bones.leftLeg.roll = -1.8188830814292183e-16f;
                    bones.leftLeg.yaw = -9.468504199578503e-17f;
                })
                .keyframe(200, Ease.INOUTQUAD, (bones, info) -> {
                    bones.head.pivot.setX(-0.0f);
                    bones.head.pivot.setY(-0.0f);
                    bones.head.pivot.setZ(-0.0f);
                    bones.head.pitch = -0.7853981852531433f;
                    bones.head.roll = 1.2363434511444637e-16f;
                    bones.head.yaw = -1.6258275967888142e-16f;
                    bones.rightArm.yaw = -0.6108652353286743f;
                    bones.rightArm.roll = 0.15707963705062866f;
                    bones.rightArm.pivot.setX(-3.4000000953674316f);
                    bones.rightArm.pitch = -1.0471975803375244f;
                    bones.leftArm.roll = -0.15707963705062866f;
                    bones.leftArm.yaw = 0.6108652353286743f;
                    bones.leftArm.pitch = -1.0471975803375244f;
                    bones.leftArm.pivot.setZ(2.0f);
                    bones.leftArm.pivot.setY(-1.0f);
                    bones.leftArm.pivot.setX(3.4000000953674316f);
                })
                .keyframe(300, Ease.INOUTQUAD, (bones, info) -> {
                    bones.head.pivot.setX(-0.0f);
                    bones.head.pivot.setY(-0.0f);
                    bones.head.pivot.setZ(-0.0f);
                    bones.head.pitch = -0.0f;
                    bones.head.roll = 2.6469779601696886e-23f;
                    bones.head.yaw = 6.617444900424222e-24f;
                    bones.body.yaw = 1.1580528575742387e-23f;
                    bones.body.roll = -0.0f;
                    bones.body.pitch = 0.0f;
                    bones.body.pivot.setX(0.0f);
                    bones.body.pivot.setY(-0.0f);
                    bones.body.pivot.setZ(-0.03125f);
                    bones.rightArm.yaw = 0.7853981852531433f;
                    bones.rightArm.roll = -1.5707963705062866f;
                    bones.rightArm.pivot.setZ(2.0f);
                    bones.rightArm.pivot.setY(-2.0f);
                    bones.rightArm.pivot.setX(-5.0f);
                    bones.rightArm.pitch = -1.5707963705062866f;
                    bones.leftArm.roll = -0.7853981852531433f;
                    bones.leftArm.yaw = 0.8726646304130554f;
                    bones.leftArm.pitch = -0.8726646304130554f;
                    bones.leftArm.pivot.setZ(2.0f);
                    bones.leftArm.pivot.setY(-1.0f);
                    bones.leftArm.pivot.setX(5.0f);
                    bones.rightLeg.pivot.setX(-1.9f);
                    bones.rightLeg.pivot.setY(0.1f);
                    bones.rightLeg.pivot.setZ(12.0f);
                    bones.rightLeg.pitch = 0.0f;
                    bones.rightLeg.roll = -6.617444900424222e-24f;
                    bones.rightLeg.yaw = 1.3234889800848443e-23f;
                    bones.leftLeg.pivot.setX(1.9f);
                    bones.leftLeg.pivot.setY(-0.1f);
                    bones.leftLeg.pivot.setZ(12.0f);
                    bones.leftLeg.pitch = 0.0f;
                    bones.leftLeg.roll = -6.617444900424222e-24f;
                    bones.leftLeg.yaw = 1.3234889800848443e-23f;
                })
                .build();
    }
}
