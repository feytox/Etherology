package name.uwu.feytox.lotyh.blocks.crucible;

import name.uwu.feytox.lotyh.util.LIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CrucibleBlockModel extends AnimatedGeoModel<CrucibleBlockEntity> {
    @Override
    public Identifier getModelResource(CrucibleBlockEntity object) {
        return new LIdentifier("geo/crucible.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrucibleBlockEntity object) {
        String colorName = object.getCurrentColor();
        if (!colorName.equals("clear")) {
            return new LIdentifier("textures/machines/crucible_" + colorName + ".png");
        }
        return new LIdentifier("textures/machines/crucible.png");
    }

    @Override
    public Identifier getAnimationResource(CrucibleBlockEntity animatable) {
        return new LIdentifier("animations/crucible.animation.json");
    }
}
