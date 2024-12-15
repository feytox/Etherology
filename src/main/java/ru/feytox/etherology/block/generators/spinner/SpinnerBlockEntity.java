package ru.feytox.etherology.block.generators.spinner;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.generators.AbstractGeneratorBlockEntity;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.SPINNER_BLOCK_ENTITY;

public class SpinnerBlockEntity extends AbstractGeneratorBlockEntity {

    private static final RawAnimation SPIN_ANIM = RawAnimation.begin().thenLoop("animation.spinner.spin");
    private static final RawAnimation STALLED_ANIM = RawAnimation.begin().thenLoop("animation.spinner.stalled");

    public SpinnerBlockEntity(BlockPos pos, BlockState state) {
        super(SPINNER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public RawAnimation getSpinAnimation() {
        return SPIN_ANIM;
    }

    @Override
    public RawAnimation getStalledAnimation() {
        return STALLED_ANIM;
    }

    @Override
    public Optional<Class<? extends TickableBlockEntity>> getClientTickerProvider() {
        return Optional.of(AbstractGeneratorBlockEntity.class);
    }
}
