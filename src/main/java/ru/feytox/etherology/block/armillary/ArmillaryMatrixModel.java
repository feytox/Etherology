package ru.feytox.etherology.block.armillary;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class ArmillaryMatrixModel extends GeoModel<ArmillaryMatrixBlockEntity> {
    @Override
    public Identifier getModelResource(ArmillaryMatrixBlockEntity animatable) {
        return new EIdentifier("geo/armillary_matrix.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArmillaryMatrixBlockEntity object) {
        return new EIdentifier("textures/machines/armillary_matrix.png");
    }

    @Override
    public Identifier getAnimationResource(ArmillaryMatrixBlockEntity animatable) {
        return new EIdentifier("animations/armillary_matrix.animation.json");
    }
}
