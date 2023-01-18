package name.uwu.feytox.etherology.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

@Mixin(GeoModel.class)
public interface GeoModelAccessor {
    @Accessor
    BakedGeoModel getCurrentModel();

    @Accessor
    void setCurrentModel(BakedGeoModel currentModel);
}
