package ru.feytox.etherology.block.crate;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class CrateBlockModel extends GeoModel<CrateBlockEntity> {
    @Override
    public Identifier getModelResource(CrateBlockEntity animatable) {
        return new EIdentifier("geo/crate.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrateBlockEntity animatable) {
        return new EIdentifier("textures/block/crate.png");
    }

    @Override
    public Identifier getAnimationResource(CrateBlockEntity animatable) {
        return new EIdentifier("animations/crate.animation.json");
    }
}
