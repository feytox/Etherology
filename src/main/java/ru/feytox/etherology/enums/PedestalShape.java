package ru.feytox.etherology.enums;

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

    public static PedestalShape getShape(BlockState underState, BlockState upperState) {
        boolean isUnder = underState.isOf(EBlocks.PEDESTAL_BLOCK);
        boolean isUpper = upperState.isOf(EBlocks.PEDESTAL_BLOCK);
        if (isUnder) {
            if (isUpper) return MIDDLE;
            return TOP;
        }
        if (isUpper) return BOTTOM;
        return FULL;
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
