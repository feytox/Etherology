package name.uwu.feytox.etherology.blocks.crucible;

import name.uwu.feytox.etherology.util.EIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CrucibleBlockItemModel extends AnimatedGeoModel<CrucibleBlockItem> {
    @Override
    public Identifier getModelResource(CrucibleBlockItem object) {
        return new EIdentifier("geo/crucible.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrucibleBlockItem object) {
        return new EIdentifier("textures/machines/crucible.png");
    }

    @Override
    public Identifier getAnimationResource(CrucibleBlockItem animatable) {
        return new EIdentifier("animations/crucible.animation.json");
    }
}
