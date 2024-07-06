package ru.feytox.etherology.block.brewingCauldron;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class BrewingCauldronModel extends GeoModel<BrewingCauldronBlockEntity> {
    @Override
    public Identifier getModelResource(BrewingCauldronBlockEntity animatable) {
        return EIdentifier.of("geo/brewing_cauldron.geo.json");
    }

    @Override
    public Identifier getTextureResource(BrewingCauldronBlockEntity animatable) {
        return EIdentifier.of("textures/block/brewing_cauldron.png");
    }

    @Override
    public Identifier getAnimationResource(BrewingCauldronBlockEntity animatable) {
        return EIdentifier.of("animations/brewing_cauldron.animation.json");
    }
}
