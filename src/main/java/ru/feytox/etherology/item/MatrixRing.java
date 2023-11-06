package ru.feytox.etherology.item;

import lombok.Getter;
import ru.feytox.etherology.enums.RingType;
import ru.feytox.etherology.util.deprecated.SimpleItem;

public class MatrixRing extends SimpleItem {
    @Getter
    private final RingType ringType;

    public MatrixRing(String itemId, RingType ringType) {
        super(itemId);
        this.ringType = ringType;
    }

    public static class EthrilMatrixRing extends MatrixRing {
        public EthrilMatrixRing() {
            super("ethril_matrix_ring", RingType.ETHRIL);
        }
    }

    public static class TelderSteelMatrixRing extends MatrixRing {
        public TelderSteelMatrixRing() {
            super("telder_steel_matrix_ring", RingType.TELDER_STEEL);
        }
    }

    public static class NetheriteMatrixRing extends MatrixRing {
        public NetheriteMatrixRing() {
            super("netherite_matrix_ring", RingType.NETHERITE);
        }
    }
}
