package ru.feytox.etherology.block.armillar;

@Deprecated
public enum RingType {
    ETHRIL(0, 0, 0, 73),
    TELDER_STEEL(0, 138, 0, 211),
    NETHERITE(0, 276, 0, 349);

    private final int u1;
    private final int v1;
    private final int u2;
    private final int v2;

    RingType(int u1, int v1, int u2, int v2) {
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public int getU1() {
        return u1;
    }

    public int getV1() {
        return v1;
    }

    public int getU2() {
        return u2;
    }

    public int getV2() {
        return v2;
    }
}
