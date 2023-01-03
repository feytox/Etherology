package name.uwu.feytox.etherology.blocks.ringMatrix;

import name.uwu.feytox.etherology.blocks.armillar.ArmillaryMatrixBlockEntity;
import name.uwu.feytox.etherology.enums.RingType;
import name.uwu.feytox.etherology.mixin.AnimatedGeoModelAccessor;
import name.uwu.feytox.etherology.util.EIdentifier;
import name.uwu.feytox.etherology.util.RingIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.List;

public class RingMatrixBlockModel extends AnimatedGeoModel<RingMatrixBlockEntity> {
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
    public GeoModel getModel(Identifier location) {
        // короче, далее сделай генерацию и кэширование моделек. Для этого ВЫШЕ передавай какой-то другой идентифаер
        // потом тут проверяй, точно ли это тот идентифаер и всё же производи генерацию по полученным данным
        // после этого заменяй модельку прямо тут и - готово (наверное)

        if (!(location instanceof RingIdentifier ringLocation)) return super.getModel(location);

        GeoModel currentModel = ((AnimatedGeoModelAccessor) this).getCurrentModel();
        AnimationProcessor animationProcessor = ((AnimatedGeoModelAccessor) this).getAnimationProcessor();
        GeoModel model = RingsModelProvider.INSTANCE.generateModel(ringLocation);

        if (model == null) return super.getModel(location);

        if (model != currentModel) {
            animationProcessor.clearModelRendererList();
            ((AnimatedGeoModelAccessor) this).setCurrentModel(model);

            for (GeoBone bone : model.topLevelBones) {
                registerBone(bone);
            }
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
