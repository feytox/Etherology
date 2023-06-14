package ru.feytox.etherology.block.etherealGenerators.metronome;

import net.minecraft.block.FacingBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import software.bernie.geckolib.model.GeoModel;

public class EtherealMetronomeModel extends GeoModel<EtherealMetronomeBlockEntity> {
    @Override
    public Identifier getModelResource(EtherealMetronomeBlockEntity animatable) {
        Direction direction = animatable.getCachedState().get(FacingBlock.FACING);
        return new EIdentifier("geo/metronome_" + direction.toString() + ".geo.json");
    }

    @Override
    public Identifier getTextureResource(EtherealMetronomeBlockEntity animatable) {
        return new EIdentifier("textures/machines/ethereal_metronome.png");
    }

    @Override
    public Identifier getAnimationResource(EtherealMetronomeBlockEntity animatable) {
        return new EIdentifier("animations/metronome.animation.json");
    }
}
