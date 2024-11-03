package ru.feytox.etherology.block.generators.spinner;

import net.minecraft.block.FacingBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class SpinnerModel extends GeoModel<SpinnerBlockEntity> {
    @Override
    public Identifier getModelResource(SpinnerBlockEntity animatable) {
        Direction direction = animatable.getCachedState().get(FacingBlock.FACING);
        return EIdentifier.of("geo/spinner_" + direction.toString() + ".geo.json");
    }

    @Override
    public Identifier getTextureResource(SpinnerBlockEntity animatable) {
        return EIdentifier.of("textures/machines/spinner.png");
    }

    @Override
    public Identifier getAnimationResource(SpinnerBlockEntity animatable) {
        return EIdentifier.of("animations/spinner.animation.json");
    }
}
