package ru.feytox.etherology.registry.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.item.*;
import ru.feytox.etherology.item.MatrixRing.EthrilMatrixRing;
import ru.feytox.etherology.item.MatrixRing.NetheriteMatrixRing;
import ru.feytox.etherology.item.MatrixRing.TelderSteelMatrixRing;
import ru.feytox.etherology.item.glints.GlintItem;
import ru.feytox.etherology.magic.staff.StaffStyles;
import ru.feytox.etherology.util.feyapi.EIdentifier;


public class EItems {

    public static final Teldecore TELDECORE = (Teldecore) new Teldecore().register();

    public static final EthrilMatrixRing ETHRIL_MATRIX_RING = (EthrilMatrixRing) new EthrilMatrixRing().register();
    public static final TelderSteelMatrixRing TELDER_STEEL_MATRIX_RING = (TelderSteelMatrixRing) new TelderSteelMatrixRing().register();
    public static final NetheriteMatrixRing NETHERITE_MATRIX_RING = (NetheriteMatrixRing) new NetheriteMatrixRing().register();
    public static final PrimoShard.KetaPrimoShard PRIMOSHARD_KETA = (PrimoShard.KetaPrimoShard) new PrimoShard.KetaPrimoShard().register();
    public static final PrimoShard.RellaPrimoShard PRIMOSHARD_RELLA = (PrimoShard.RellaPrimoShard) new PrimoShard.RellaPrimoShard().register();
    public static final PrimoShard.ClosPrimoShard PRIMOSHARD_CLOS = (PrimoShard.ClosPrimoShard) new PrimoShard.ClosPrimoShard().register();
    public static final PrimoShard.ViaPrimoShard PRIMOSHARD_VIA = (PrimoShard.ViaPrimoShard) new PrimoShard.ViaPrimoShard().register();
    public static final GlintItem GLINT = (GlintItem) new GlintItem().register();
    public static final EtherShard ETHER_SHARD = (EtherShard) new EtherShard().register();
    public static final EtherOil ETHEREAL_OIL = (EtherOil) new EtherOil().register();
    public static final SpillBarrelItem SPILL_BARREL = (SpillBarrelItem) registerItem("spill_barrel", new SpillBarrelItem());
    public static final CarriedCrateItem CARRIED_CRATE = (CarriedCrateItem) registerItem("carried_crate", new CarriedCrateItem());
    public static final CorruptionBucket CORRUPTION_BUCKET = (CorruptionBucket) registerItem("corruption_bucket", new CorruptionBucket());

    public static final PatternTabletItem ARISTOCRAT_PATTERN_TABLET = (PatternTabletItem) registerItem("aristocrat_pattern_tablet", new PatternTabletItem(StaffStyles.ARISTOCRAT));
    public static final PatternTabletItem ASTRONOMY_PATTERN_TABLET = (PatternTabletItem) registerItem("astronomy_pattern_tablet", new PatternTabletItem(StaffStyles.ASTRONOMY));
    public static final PatternTabletItem HEAVENLY_PATTERN_TABLET = (PatternTabletItem) registerItem("heavenly_pattern_tablet", new PatternTabletItem(StaffStyles.HEAVENLY));
    public static final PatternTabletItem OCULAR_PATTERN_TABLET = (PatternTabletItem) registerItem("ocular_pattern_tablet", new PatternTabletItem(StaffStyles.OCULAR));
    public static final PatternTabletItem RITUAL_PATTERN_TABLET = (PatternTabletItem) registerItem("ritual_pattern_tablet", new PatternTabletItem(StaffStyles.RITUAL));
    public static final PatternTabletItem ROYAL_PATTERN_TABLET = (PatternTabletItem) registerItem("royal_pattern_tablet", new PatternTabletItem(StaffStyles.ROYAL));
    public static final PatternTabletItem TRADITIONAL_PATTERN_TABLET = (PatternTabletItem) registerItem("traditional_pattern_tablet", new PatternTabletItem(StaffStyles.TRADITIONAL));
    public static final PatternTabletItem[] PATTERN_TABLETS = {ARISTOCRAT_PATTERN_TABLET, ASTRONOMY_PATTERN_TABLET, HEAVENLY_PATTERN_TABLET, OCULAR_PATTERN_TABLET, RITUAL_PATTERN_TABLET, ROYAL_PATTERN_TABLET, TRADITIONAL_PATTERN_TABLET};

    private static Item registerItem(String itemId, Item item) {
        return Registry.register(Registries.ITEM, new EIdentifier(itemId), item);
    }

    public static void registerItems() {
        // TODO: 29.10.2023 проверить необходимость
        DecoBlockItems.registerAll();
        ToolItems.registerAll();
        ArmorItems.registerAll();
    }
}
