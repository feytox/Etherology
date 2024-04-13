package ru.feytox.etherology.model.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class RevelationViewModel extends BipedEntityModel<LivingEntity> {

    public RevelationViewModel(ModelPart root) {
        super(root);
        setVisible(false);
        head.visible = true;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-4F, -8F, -4F, 8F, 8F, 8F, new Dilation(0.5F)), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }
}
