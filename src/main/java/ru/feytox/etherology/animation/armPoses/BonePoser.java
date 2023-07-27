package ru.feytox.etherology.animation.armPoses;


import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface BonePoser {

    void accept(PlayerBones bones, LivingEntity entity, boolean isRightArm, float percent);
}
