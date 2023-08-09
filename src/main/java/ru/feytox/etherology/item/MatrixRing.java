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

    public static class EthrilRing extends MatrixRing {
        public EthrilRing() {
            super("ethril_ring", RingType.ETHRIL);
        }
    }

    public static class TelderSteelRing extends MatrixRing {
        public TelderSteelRing() {
            super("telder_steel_ring", RingType.TELDER_STEEL);
        }
    }

    public static class NetheriteRing extends MatrixRing {
        public NetheriteRing() {
            super("netherite_ring", RingType.NETHERITE);
        }
    }
}
