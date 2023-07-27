package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;

@UtilityClass
public class ArmAnimations {

    public static final FeyAnimation TEST_ANIMATION;

    static {
        TEST_ANIMATION = FeyAnimation.Builder.create(200)
                .keyframe(0, ((bones, entity, isRightArm, percent) ->
                        bones.leftArm.getPitch().setValue(-MathHelper.PI * percent * (1 + 1/2f))))
                .keyframe(100, Ease.INBOUNCE, ((bones, entity, isRightArm, percent) ->
                        bones.leftArm.getPitch().setValue(-MathHelper.PI * percent * (2 + 1/2f))))
                .build();
    }
}
