package ru.feytox.etherology.block.pedestal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.registry.block.EBlocks;

@RequiredArgsConstructor
@Getter
public enum PedestalShape implements StringIdentifiable {
    BOTTOM(false),
    MIDDLE(false),
    TOP(true),
    FULL(true);

    private final boolean hasItem;

    public static PedestalShape getShape(BlockState underState, BlockState topState) {
        boolean isUnder = underState.isOf(EBlocks.PEDESTAL_BLOCK);
        boolean isTop = topState.isOf(EBlocks.PEDESTAL_BLOCK);
        if (isUnder) {
            if (isTop) return MIDDLE;
            return TOP;
        }
        if (isTop) return BOTTOM;
        return FULL;
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
