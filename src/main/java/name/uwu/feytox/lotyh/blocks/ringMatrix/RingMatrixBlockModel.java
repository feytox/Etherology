package name.uwu.feytox.lotyh.blocks.ringMatrix;

import name.uwu.feytox.lotyh.util.LIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RingMatrixBlockModel extends AnimatedGeoModel<RingMatrixBlockEntity> {
    @Override
    public Identifier getModelResource(RingMatrixBlockEntity object) {
        return new LIdentifier("geo/ring_matrix.geo.json");
    }

    @Override
    public Identifier getTextureResource(RingMatrixBlockEntity object) {
        return new LIdentifier("textures/machines/ethril_ring_1.png");
    }

    @Override
    public Identifier getAnimationResource(RingMatrixBlockEntity animatable) {
        return new LIdentifier("animations/ring_matrix.animation.json");
    }
}
