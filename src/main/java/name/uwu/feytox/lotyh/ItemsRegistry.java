package name.uwu.feytox.lotyh;

import name.uwu.feytox.lotyh.blocks.crucible.CrucibleBlockItem;
import name.uwu.feytox.lotyh.items.*;
import name.uwu.feytox.lotyh.util.LIdentifier;
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

    public static final Item CRUCIBLE_BLOCK_ITEM = registerItem("crucible_block_item",
            new CrucibleBlockItem(BlocksRegistry.CRUCIBLE, new FabricItemSettings().group(ItemGroup.MISC)));
    public static final Item RING_MATRIX_BLOCK_ITEM = registerItem("ring_matrix_block_item",
            new CrucibleBlockItem(BlocksRegistry.CRUCIBLE, new FabricItemSettings().group(ItemGroup.MISC)));


    private static Item registerItem(String itemId, Item item) {
        return Registry.register(Registry.ITEM, new LIdentifier(itemId), item);
    }

    public static void registerItems() {
    }
}
