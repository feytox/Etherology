package ru.feytox.etherology.block.thuja;

import net.minecraft.block.BlockState;
import net.minecraft.util.StringIdentifiable;

public enum ThujaShape implements StringIdentifiable {
    STEM,
    ROOT,
    CROWN,
    BUSH;

    public static ThujaShape getShape(BlockState underState, BlockState topState) {
        boolean isUnder = underState.getBlock() instanceof ThujaShapeController;
        boolean isTop = topState.getBlock() instanceof ThujaShapeController;
        if (isUnder) {
            if (isTop) return STEM;
            return CROWN;
        }
        if (isTop) return ROOT;
        return BUSH;
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
