package ru.feytox.etherology.animation;

import net.minecraft.client.render.entity.model.BipedEntityModel;


public record PartsInfo(float headY, float bodyY, float leftArmY, float rightArmY, float leftLegY, float leftLegZ, float rightLegY, float rightLegZ) {

    /**
     * Constructs a new PartsInfo object from a BipedEntityModel.
     *
     * @param  model  the BipedEntityModel to extract the parts information from
     * @return        a PartsInfo object containing the pivot Y and Z values for each part of the model
     */
    public static PartsInfo of(BipedEntityModel<?> model) {
        return new PartsInfo(model.head.pivotY, model.body.pivotY, model.leftArm.pivotY, model.rightArm.pivotY, model.leftLeg.pivotY, model.leftLeg.pivotZ, model.rightLeg.pivotY, model.rightLeg.pivotZ);
    }
}
