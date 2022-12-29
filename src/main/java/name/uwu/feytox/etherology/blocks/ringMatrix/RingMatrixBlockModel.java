package name.uwu.feytox.etherology.blocks.ringMatrix;

import name.uwu.feytox.etherology.util.EIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RingMatrixBlockModel extends AnimatedGeoModel<RingMatrixBlockEntity> {
    @Override
    public Identifier getModelResource(RingMatrixBlockEntity object) {
        return new EIdentifier("geo/ring_matrix.geo.json");
    }

    // uncomment and дополнить after rename
//    @Override
//    public GeoModel getModel(Identifier location) {
//        // короче, далее сделай генерацию и кэширование моделек. Для этого ВЫШЕ передавай какой-то другой идентифаер
//        // потом тут проверяй, точно ли это тот идентифаер и всё же производи генерацию по полученным данным
//        // после этого заменяй модельку прямо тут и - готово (наверное)
//
//        GeoModel currentModel = ((AnimatedGeoModelAccessor) this).getCurrentModel();
//        AnimationProcessor animationProcessor = ((AnimatedGeoModelAccessor) this).getAnimationProcessor();
//
//        if (model != currentModel) {
//            animationProcessor.clearModelRendererList();
//            currentModel = model;
//
//            for (GeoBone bone : model.topLevelBones) {
//                registerBone(bone);
//            }
//        }
//
//        return model;
//    }

    @Override
    public Identifier getTextureResource(RingMatrixBlockEntity object) {
        return new EIdentifier("textures/machines/ethril_ring_1.png");
    }

    @Override
    public Identifier getAnimationResource(RingMatrixBlockEntity animatable) {
        return new EIdentifier("animations/ring_matrix.animation.json");
    }
}
