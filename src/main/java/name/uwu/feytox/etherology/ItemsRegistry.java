package name.uwu.feytox.etherology;

import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlockItem;
import name.uwu.feytox.etherology.items.*;
import name.uwu.feytox.etherology.items.MatrixRing.EthrilRing;
import name.uwu.feytox.etherology.items.MatrixRing.NetheriteRing;
import name.uwu.feytox.etherology.items.MatrixRing.TelderSteelRing;
import name.uwu.feytox.etherology.util.EIdentifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;


public class ItemsRegistry {

    public static final Teldecore TELDECORE = (Teldecore) new Teldecore().register();

    public static final DeepShard DEEP_SHARD = (DeepShard) new DeepShard().register();
    public static final TerrestrialShard TERRESTRIAL_SHARD = (TerrestrialShard) new TerrestrialShard().register();
    public static final HeavenlyShard HEAVENLY_SHARD = (HeavenlyShard) new HeavenlyShard().register();
    public static final AquaticShard AQUATIC_SHARD = (AquaticShard) new AquaticShard().register();
    public static final EthrilRing ETHRIL_RING = (EthrilRing) new EthrilRing().register();
    public static final TelderSteelRing TELDER_STEEL_RING = (TelderSteelRing) new TelderSteelRing().register();
    public static final NetheriteRing NETHERITE_RING = (NetheriteRing) new NetheriteRing().register();

    public static final Item CRUCIBLE_BLOCK_ITEM = registerItem("crucible_block_item",
            new CrucibleBlockItem(BlocksRegistry.CRUCIBLE, new FabricItemSettings().group(ItemGroup.MISC)));


    private static Item registerItem(String itemId, Item item) {
        return Registry.register(Registry.ITEM, new EIdentifier(itemId), item);
    }

    public static void registerItems() {
    }
}
