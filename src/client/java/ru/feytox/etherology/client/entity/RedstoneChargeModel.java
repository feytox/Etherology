package ru.feytox.etherology.client.entity;

import net.minecraft.util.Identifier;
import ru.feytox.etherology.entity.RedstoneChargeEntity;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class RedstoneChargeModel extends GeoModel<RedstoneChargeEntity> {

    @Override
    public Identifier getModelResource(RedstoneChargeEntity animatable) {
        return EIdentifier.of("geo/redstone_charge.geo.json");
    }

    @Override
    public Identifier getTextureResource(RedstoneChargeEntity animatable) {
        return EIdentifier.of("textures/entity/redstone_charge.png");
    }

    @Override
    public Identifier getAnimationResource(RedstoneChargeEntity animatable) {
        return EIdentifier.of("animations/redstone_charge.animation.json");
    }
}
