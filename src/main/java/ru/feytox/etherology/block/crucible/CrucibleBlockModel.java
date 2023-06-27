package ru.feytox.etherology.block.crucible;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class CrucibleBlockModel extends GeoModel<CrucibleBlockEntity> {

    @Override
    public Identifier getModelResource(CrucibleBlockEntity animatable) {
        return new EIdentifier("geo/crucible.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrucibleBlockEntity animatable) {
        // TODO: 27.06.2023 adapt for texture animation
        return new EIdentifier("textures/machines/crucible.png");
    }

    @Override
    public Identifier getAnimationResource(CrucibleBlockEntity animatable) {
        return new EIdentifier("animations/crucible.animation.json");
    }
}
