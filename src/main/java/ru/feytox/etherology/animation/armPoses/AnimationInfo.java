package ru.feytox.etherology.animation.armPoses;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

public record AnimationInfo(BipedEntityModel<?> model, LivingEntity entity, float percent) {
}
