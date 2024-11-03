package ru.feytox.etherology.block.generators.metronome;

import net.minecraft.block.FacingBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.util.misc.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class MetronomeModel extends GeoModel<MetronomeBlockEntity> {
    @Override
    public Identifier getModelResource(MetronomeBlockEntity animatable) {
        Direction direction = animatable.getCachedState().get(FacingBlock.FACING);
        return EIdentifier.of("geo/metronome_" + direction.toString() + ".geo.json");
    }

    @Override
    public Identifier getTextureResource(MetronomeBlockEntity animatable) {
        return EIdentifier.of("textures/machines/metronome.png");
    }

    @Override
    public Identifier getAnimationResource(MetronomeBlockEntity animatable) {
        return EIdentifier.of("animations/metronome.animation.json");
    }
}
