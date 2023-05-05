package ru.feytox.etherology.block.etherealGenerators.spinner;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.etherealGenerators.AbstractEtherealGeneratorBlockEntity;
import software.bernie.geckolib.core.animation.RawAnimation;

import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_SPINNER_BLOCK_ENTITY;

public class EtherealSpinnerBlockEntity extends AbstractEtherealGeneratorBlockEntity {
    private static final RawAnimation SPIN_ANIM = RawAnimation.begin().thenLoop("animation.spinner.spin");
    private static final RawAnimation STALLED_ANIM = RawAnimation.begin().thenLoop("animation.spinner.stalled");

    public EtherealSpinnerBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_SPINNER_BLOCK_ENTITY, pos, state);
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
