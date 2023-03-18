package ru.feytox.etherology.blocks.etherealStorage;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class EtherealStorageModel extends GeoModel<EtherealStorageBlockEntity> {
    @Override
    public Identifier getModelResource(EtherealStorageBlockEntity animatable) {
        return new EIdentifier("geo/ethereal_storage.geo.json");
    }

    @Override
    public Identifier getTextureResource(EtherealStorageBlockEntity animatable) {
        return new EIdentifier("textures/machines/ethereal_storage.png");
    }

    @Override
    public Identifier getAnimationResource(EtherealStorageBlockEntity animatable) {
        return new EIdentifier("animations/ethereal_storage.animation.json");
    }
}
