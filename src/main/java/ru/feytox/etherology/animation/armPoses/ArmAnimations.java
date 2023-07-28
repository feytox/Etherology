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

    static {
        TEST_ANIMATION = ArmAnimation.Builder.create(5000, 0)
                .trigger(OculusItem::isUsingOculus)
                .keyframe(0, Ease.INCUBIC, ((bones, info) ->
                        bones.leftArm.pitch = -MathHelper.PI * info.percent() * 2))
                .build();
        TEST_ANIMATION_2 = ArmAnimation.Builder.create(2500, 1)
                .trigger(Entity::isSneaking)
                .animateArms(false)
                .keyframe(0, ((bones, info) ->
                        bones.leftArm.pitch = -MathHelper.PI * info.percent() * 2))
                .build();
    }
}
