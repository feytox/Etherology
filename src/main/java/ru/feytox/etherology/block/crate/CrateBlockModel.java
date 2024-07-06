package ru.feytox.etherology.block.crate;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class CrateBlockModel extends GeoModel<CrateBlockEntity> {
    @Override
    public Identifier getModelResource(CrateBlockEntity animatable) {
        return EIdentifier.of("geo/crate.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrateBlockEntity animatable) {
        return EIdentifier.of("textures/block/crate.png");
    }

    @Override
    public Identifier getAnimationResource(CrateBlockEntity animatable) {
        return EIdentifier.of("animations/crate.animation.json");
    }
}
