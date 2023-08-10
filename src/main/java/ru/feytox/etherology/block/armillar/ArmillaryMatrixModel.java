package ru.feytox.etherology.block.armillar;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.enums.RingType;
import ru.feytox.etherology.mixin.GeoModelAccessor;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.RingIdentifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;

public class ArmillaryMatrixModel extends GeoModel<ArmillaryMatrixBlockEntity> {
    @Override
    public Identifier getModelResource(ArmillaryMatrixBlockEntity animatable) {
        int ringsNum = animatable.getRingsNum();
        List<RingType> ringTypes = animatable.getRingTypes();
        return new RingIdentifier(new EIdentifier("geo/armillary_matrix.geo.json"), ringTypes, ringsNum);
    }

    @Override
    public BakedGeoModel getBakedModel(Identifier location) {
        if (!(location instanceof RingIdentifier ringLocation)) return super.getBakedModel(location);

        BakedGeoModel model = RingsModelProvider.INSTANCE.generateModel(ringLocation);
        BakedGeoModel currentModel = ((GeoModelAccessor) this).getCurrentModel();
        if (model == null) return currentModel;
        if (model.equals(currentModel)) return model;

        getAnimationProcessor().setActiveModel(model);
        ((GeoModelAccessor) this).setCurrentModel(model);
        return model;
    }

    @Override
    public Identifier getTextureResource(ArmillaryMatrixBlockEntity object) {
        return new EIdentifier("textures/machines/matrix_rings.png");
    }

    @Override
    public Identifier getAnimationResource(ArmillaryMatrixBlockEntity animatable) {
        return new EIdentifier("animations/armillary_matrix.animation.json");
    }
}
