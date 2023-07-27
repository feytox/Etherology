package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.LivingEntity;

@RequiredArgsConstructor
public class FeyKeyframe {
    private final BonePoser bonePoser;
    private final Ease easing;
    private final int startTick;
    private final int endTick;

    public boolean isKeyframe(int tick) {
        return tick < endTick && tick >= startTick;
    }

    public void tickKeyframe(int tick, PlayerBones bones, LivingEntity entity, boolean isRightArm) {
        if (!isKeyframe(tick)) return;
        bonePoser.accept(bones, entity, isRightArm, easing.invoke((tick - startTick + 0.0f) / (endTick - startTick)));
    }
}
