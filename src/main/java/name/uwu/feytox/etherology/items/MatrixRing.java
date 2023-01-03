package name.uwu.feytox.etherology.items;

import name.uwu.feytox.etherology.enums.RingType;
import name.uwu.feytox.etherology.util.SimpleItem;

public class MatrixRing extends SimpleItem {
    private final RingType ringType;

    public MatrixRing(String itemId, RingType ringType) {
        super(itemId);
        this.ringType = ringType;
    }

    public RingType getRingType() {
        return ringType;
    }

    // note: я использовал их отдельными подклассами для того, чтобы если что добавить функционал для отдельного типа колец

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
