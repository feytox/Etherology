package name.uwu.feytox.etherology.blocks.ringMatrix;

import name.uwu.feytox.etherology.blocks.armillar.ArmillaryMatrixBlockEntity;
import name.uwu.feytox.etherology.enums.RingType;
import name.uwu.feytox.etherology.mixin.GeoModelAccessor;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import name.uwu.feytox.etherology.util.feyapi.RingIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;

public class RingMatrixBlockModel extends GeoModel<RingMatrixBlockEntity> {
    @Override
    public Identifier getModelResource(RingMatrixBlockEntity object) {
        int ringsNum = 0;
        List<RingType> ringsTypes = List.of(RingType.ETHRIL);

        ArmillaryMatrixBlockEntity baseBlock = object.getBaseBlock();
        if (baseBlock != null) {
            ringsNum = baseBlock.getRingsNum();
            ringsTypes = baseBlock.getRingTypes();
        }

        return new RingIdentifier(new EIdentifier("geo/ring_matrix.geo.json"), ringsTypes, ringsNum);
    }

    @Override
    public BakedGeoModel getBakedModel(Identifier location) {
        if (!(location instanceof RingIdentifier ringLocation)) return super.getBakedModel(location);

        BakedGeoModel model = RingsModelProvider.INSTANCE.generateModel(ringLocation);

        BakedGeoModel currentModel = ((GeoModelAccessor) this).getCurrentModel();

        if (model == null) return currentModel;

        if (model != currentModel) {
            this.getAnimationProcessor().setActiveModel(model);
            ((GeoModelAccessor) this).setCurrentModel(model);
        }

        return model;
    }

    @Override
    public Identifier getTextureResource(RingMatrixBlockEntity object) {
        return new EIdentifier("textures/machines/matrix_rings.png");
    }

    @Override
    public Identifier getAnimationResource(RingMatrixBlockEntity animatable) {
        return new EIdentifier("animations/ring_matrix.animation.json");
    }
}
