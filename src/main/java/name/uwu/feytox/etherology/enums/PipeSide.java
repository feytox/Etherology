package name.uwu.feytox.etherology.enums;

import net.minecraft.util.StringIdentifiable;

public enum PipeSide implements StringIdentifiable {
    EMPTY,
    IN,
    OUT;

    public boolean isInput() {
        return this.equals(IN);
    }

    public boolean isOutput() {
        return this.equals(OUT);
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
