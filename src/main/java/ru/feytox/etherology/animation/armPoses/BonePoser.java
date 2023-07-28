package ru.feytox.etherology.animation.armPoses;


@FunctionalInterface
public interface BonePoser {

    void accept(PlayerBones bones, AnimationInfo info);
}
