package ru.feytox.etherology.client.block.etherealStorage;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageBlockEntity;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class EtherealStorageModel extends GeoModel<EtherealStorageBlockEntity> {
    @Override
    public Identifier getModelResource(EtherealStorageBlockEntity animatable) {
        return EIdentifier.of("geo/ethereal_storage.geo.json");
    }

    @Override
    public Identifier getTextureResource(EtherealStorageBlockEntity animatable) {
        return EIdentifier.of("textures/machines/ethereal_storage.png");
    }

    @Override
    public Identifier getAnimationResource(EtherealStorageBlockEntity animatable) {
        return EIdentifier.of("animations/ethereal_storage.animation.json");
    }
}
