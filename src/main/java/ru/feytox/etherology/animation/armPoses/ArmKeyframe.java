package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

@RequiredArgsConstructor
public class ArmKeyframe {
    private final BonePoser bonePoser;
    private final Ease easing;
    private final float startMillis;
    private final float endMillis;

    /**
     * Determines if the given time is within the keyframe range.
     *
     * @param  timeMillis  the time in milliseconds to check
     * @return             true if the time is within the keyframe range, false otherwise
     */
    public boolean isKeyframe(float timeMillis) {
        return timeMillis < endMillis && timeMillis >= startMillis;
    }

    /**
     * Calculates the interpolated value for a keyframe animation at the specified time.
     *
     * @param  timeMillis  the time in milliseconds
     * @param  model       the model of the biped entity
     * @param  bones       the bones of the player
     * @param  entity      the living entity
     * @return             the interpolated value at the specified time
     */
    public float tickKeyframe(float timeMillis, BipedEntityModel<?> model, PlayerBones bones, LivingEntity entity) {
        if (!isKeyframe(timeMillis)) return 100.0f;
        bonePoser.accept(bones, new AnimationInfo(model, entity));
        return easing.invoke((timeMillis - startMillis) / (endMillis - startMillis));
    }
}
