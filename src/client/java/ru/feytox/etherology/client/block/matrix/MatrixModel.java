package ru.feytox.etherology.client.block.matrix;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.block.matrix.MatrixBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class MatrixModel extends GeoModel<MatrixBlockEntity> {
    @Override
    public Identifier getModelResource(MatrixBlockEntity animatable) {
        return EIdentifier.of("geo/armillary_matrix.geo.json");
    }

    @Override
    public Identifier getTextureResource(MatrixBlockEntity object) {
        return EIdentifier.of("textures/machines/armillary_matrix.png");
    }

    @Override
    public Identifier getAnimationResource(MatrixBlockEntity animatable) {
        return EIdentifier.of("animations/armillary_matrix.animation.json");
    }
}
