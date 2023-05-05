package ru.feytox.etherology.block.etherealGenerators.metronome;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.etherealGenerators.AbstractEtherealGeneratorBlockEntity;
import software.bernie.geckolib.core.animation.RawAnimation;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_METRONOME_BLOCK_ENTITY;

public class EtherealMetronomeBlockEntity extends AbstractEtherealGeneratorBlockEntity {
    private static final RawAnimation SPIN_ANIM = RawAnimation.begin().thenLoop("animation.metronome.spin");
    private static final RawAnimation STALLED_ANIM = RawAnimation.begin().thenLoop("animation.metronome.stalled");

    public EtherealMetronomeBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_METRONOME_BLOCK_ENTITY, pos, state);
    }

    @Override
    public RawAnimation getSpinAnimation() {
        return SPIN_ANIM;
    }

    @Override
    public RawAnimation getStalledAnimation() {
        return STALLED_ANIM;
    }
}
