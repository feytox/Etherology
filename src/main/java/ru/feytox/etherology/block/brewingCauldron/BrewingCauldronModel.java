package ru.feytox.etherology.block.brewingCauldron;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class BrewingCauldronModel extends GeoModel<BrewingCauldronBlockEntity> {
    @Override
    public Identifier getModelResource(BrewingCauldronBlockEntity animatable) {
        return new EIdentifier("geo/brewing_cauldron.geo.json");
    }

    @Override
    public Identifier getTextureResource(BrewingCauldronBlockEntity animatable) {
        return new EIdentifier("textures/block/brewing_cauldron.png");
    }

    @Override
    public Identifier getAnimationResource(BrewingCauldronBlockEntity animatable) {
        return new EIdentifier("animations/brewing_cauldron.animation.json");
    }
}
