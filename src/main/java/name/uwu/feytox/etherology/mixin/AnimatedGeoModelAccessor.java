package name.uwu.feytox.etherology.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(AnimatedGeoModel.class)
public interface AnimatedGeoModelAccessor {

    @Accessor
    void setCurrentModel(GeoModel currentModel);

    @Accessor
    GeoModel getCurrentModel();

    @Accessor
    AnimationProcessor getAnimationProcessor();
}
