package name.uwu.feytox.etherology.blocks.crucible;

import name.uwu.feytox.etherology.util.EIdentifier;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrucibleBlockModel extends GeoModel<CrucibleBlockEntity> {

    @Override
    public Identifier getModelResource(CrucibleBlockEntity object) {
        return new EIdentifier("geo/crucible.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrucibleBlockEntity object) {
        String colorName = object.getCurrentColor();
        if (!colorName.equals("clear")) {
            return new EIdentifier("textures/machines/crucible_" + colorName + ".png");
        }
        return new EIdentifier("textures/machines/crucible.png");
    }

    @Override
    public Identifier getAnimationResource(CrucibleBlockEntity animatable) {
        return new EIdentifier("animations/crucible.animation.json");
    }
}
