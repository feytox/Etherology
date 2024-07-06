package ru.feytox.etherology.block.armillary;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class ArmillaryMatrixModel extends GeoModel<ArmillaryMatrixBlockEntity> {
    @Override
    public Identifier getModelResource(ArmillaryMatrixBlockEntity animatable) {
        return EIdentifier.of("geo/armillary_matrix.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArmillaryMatrixBlockEntity object) {
        return EIdentifier.of("textures/machines/armillary_matrix.png");
    }

    @Override
    public Identifier getAnimationResource(ArmillaryMatrixBlockEntity animatable) {
        return EIdentifier.of("animations/armillary_matrix.animation.json");
    }
}
