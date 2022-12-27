package name.uwu.feytox.lotyh.blocks.crucible;

import name.uwu.feytox.lotyh.util.LIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CrucibleBlockItemModel extends AnimatedGeoModel<CrucibleBlockItem> {
    @Override
    public Identifier getModelResource(CrucibleBlockItem object) {
        return new LIdentifier("geo/crucible.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrucibleBlockItem object) {
        return new LIdentifier("textures/machines/crucible.png");
    }

    @Override
    public Identifier getAnimationResource(CrucibleBlockItem animatable) {
        return new LIdentifier("animations/crucible.animation.json");
    }
}
