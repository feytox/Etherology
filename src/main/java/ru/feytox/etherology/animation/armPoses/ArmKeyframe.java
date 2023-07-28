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

    public boolean isKeyframe(float timeMillis) {
        return timeMillis < endMillis && timeMillis >= startMillis;
    }

    public void tickKeyframe(float timeMillis, BipedEntityModel<?> model, PlayerBones bones, LivingEntity entity) {
        if (!isKeyframe(timeMillis)) return;
        bonePoser.accept(bones, new AnimationInfo(model, entity, easing.invoke((timeMillis - startMillis) / (endMillis - startMillis))));
    }
}
