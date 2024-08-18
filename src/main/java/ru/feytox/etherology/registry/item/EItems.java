package ru.feytox.etherology.registry.item;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.item.*;
import ru.feytox.etherology.item.glints.GlintItem;
import ru.feytox.etherology.magic.staff.StaffStyles;
import ru.feytox.etherology.util.misc.BoatTypes;
import ru.feytox.etherology.util.misc.EIdentifier;


public class EItems {

    public static final Item TELDECORE = registerItem("teldecore", new Teldecore());

    public static final Item PRIMOSHARD_KETA = registerItem("primoshard_keta", new PrimoShard("Keta"));
    public static final Item PRIMOSHARD_RELLA = registerItem("primoshard_rella", new PrimoShard("Rella"));
    public static final Item PRIMOSHARD_CLOS = registerItem("primoshard_clos", new PrimoShard("Clos"));
    public static final Item PRIMOSHARD_VIA = registerItem("primoshard_via", new PrimoShard("Via"));
    public static final Item GLINT = registerItem("glint_shard", new GlintItem(128));
    public static final Item ETHER_SHARD = registerSimple("ether_shard");
    public static final Item SPILL_BARREL = registerItem("spill_barrel", new SpillBarrelItem());
    public static final Item CORRUPTION_BUCKET = registerItem("corruption_bucket", new CorruptionBucket());

    public static final Item ARISTOCRAT_PATTERN_TABLET = registerItem("aristocrat_pattern_tablet", new PatternTabletItem(StaffStyles.ARISTOCRAT));
    public static final Item ASTRONOMY_PATTERN_TABLET = registerItem("astronomy_pattern_tablet", new PatternTabletItem(StaffStyles.ASTRONOMY));
    public static final Item HEAVENLY_PATTERN_TABLET = registerItem("heavenly_pattern_tablet", new PatternTabletItem(StaffStyles.HEAVENLY));
    public static final Item OCULAR_PATTERN_TABLET = registerItem("ocular_pattern_tablet", new PatternTabletItem(StaffStyles.OCULAR));
    public static final Item RITUAL_PATTERN_TABLET = registerItem("ritual_pattern_tablet", new PatternTabletItem(StaffStyles.RITUAL));
    public static final Item ROYAL_PATTERN_TABLET = registerItem("royal_pattern_tablet", new PatternTabletItem(StaffStyles.ROYAL));
    public static final Item TRADITIONAL_PATTERN_TABLET = registerItem("traditional_pattern_tablet", new PatternTabletItem(StaffStyles.TRADITIONAL));
    public static final Item[] PATTERN_TABLETS = {ARISTOCRAT_PATTERN_TABLET, ASTRONOMY_PATTERN_TABLET, HEAVENLY_PATTERN_TABLET, OCULAR_PATTERN_TABLET, RITUAL_PATTERN_TABLET, ROYAL_PATTERN_TABLET, TRADITIONAL_PATTERN_TABLET};

    public static final Item UNADJUSTED_LENS = registerItem("unadjusted_lens", new UnadjustedLens());
    public static final Item REDSTONE_LENS = registerItem("redstone_lens", new RedstoneLens());
    public static final Item[] LENSES = {REDSTONE_LENS};

    public static final Item ETHEROSCOPE = registerSimple("etheroscope");
    public static final Item THUJA_OIL = registerSimple("thuja_oil");

    public static final Item FOREST_LANTERN_CRUMB = registerItem("forest_lantern_crumb", new Item(new Item.Settings().food(EFoodComponents.CRUMB)));

    public static final Item PEACH_BOAT = registerItem("peach_boat", new BoatItem(false, BoatTypes.PEACH_TYPE.get(), new Item.Settings().maxCount(1)));
    public static final Item PEACH_CHEST_BOAT = registerItem("peach_chest_boat", new BoatItem(true, BoatTypes.PEACH_TYPE.get(), new Item.Settings().maxCount(1)));

    private static Item registerItem(String itemId, Item item) {
        return Registry.register(Registries.ITEM, EIdentifier.of(itemId), item);
    }

    private static Item registerSimple(String itemId) {
        return registerItem(itemId, new Item(new Item.Settings()));
    }

    public static void registerItems() {
        DecoBlockItems.registerAll();
        ToolItems.registerAll();
        ArmorItems.registerAll();
        registerFuel();
    }

    private static void registerFuel() {
        FuelRegistry.INSTANCE.add(THUJA_OIL, 200);
        FuelRegistry.INSTANCE.add(ToolItems.WOODEN_BATTLE_PICKAXE, 200);
    }
}
