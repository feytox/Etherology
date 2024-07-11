package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import ru.feytox.etherology.registry.item.EItems;

import static net.minecraft.loot.LootTables.*;

@UtilityClass
public class LootTablesModifyRegistry {

    public static void registerAll() {
        LootTableEvents.MODIFY.register(LootTablesModifyRegistry::registerModifications);
    }

    private static void registerModifications(RegistryKey<LootTable> key, LootTable.Builder builder, LootTableSource lootTableSource) {
        if (injectTabletPattern(key.equals(BASTION_OTHER_CHEST) || key.equals(BASTION_BRIDGE_CHEST) || key.equals(BASTION_TREASURE_CHEST) || key.equals(BASTION_HOGLIN_STABLE_CHEST), EItems.ROYAL_PATTERN_TABLET, builder)) return;
        if (injectTabletPattern(key.equals(WOODLAND_MANSION_CHEST), EItems.ARISTOCRAT_PATTERN_TABLET, builder)) return;
        if (injectTabletPattern(key.equals(DESERT_PYRAMID_CHEST), EItems.RITUAL_PATTERN_TABLET, builder)) return;
        if (injectTabletPattern(key.equals(END_CITY_TREASURE_CHEST), EItems.OCULAR_PATTERN_TABLET, builder)) return;
        if (injectTabletPattern(key.equals(SHIPWRECK_TREASURE_CHEST), EItems.HEAVENLY_PATTERN_TABLET, builder)) return;
        injectTabletPattern(key.equals(JUNGLE_TEMPLE_CHEST), EItems.ASTRONOMY_PATTERN_TABLET, builder);
    }

    private static boolean injectTabletPattern(boolean idTest, Item patternTablet, LootTable.Builder tableBuilder) {
        if (!idTest) return false;

        LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(patternTablet).weight(5)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f))));
        tableBuilder.pool(pool);
        return true;
    }
}
