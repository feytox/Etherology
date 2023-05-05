package ru.feytox.etherology.block.etherealGenerators.spinner;

import net.minecraft.block.FacingBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class EtherealSpinnerModel extends GeoModel<EtherealSpinnerBlockEntity> {
    @Override
    public Identifier getModelResource(EtherealSpinnerBlockEntity animatable) {
        Direction direction = animatable.getCachedState().get(FacingBlock.FACING);
        return new EIdentifier("geo/spinner_" + direction.toString() + ".geo.json");
    }

    @Override
    public Identifier getTextureResource(EtherealSpinnerBlockEntity animatable) {
        return new EIdentifier("textures/machines/spinner.png");
    }

    @Override
    public Identifier getAnimationResource(EtherealSpinnerBlockEntity animatable) {
        return new EIdentifier("animations/spinner.animation.json");
    }
}
