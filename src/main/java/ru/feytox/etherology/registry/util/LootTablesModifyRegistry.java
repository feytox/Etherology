package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.PatternTabletItem;
import ru.feytox.etherology.registry.item.EItems;

import static net.minecraft.loot.LootTables.*;

@UtilityClass
public class LootTablesModifyRegistry {

    public static void registerAll() {
        LootTableEvents.MODIFY.register(LootTablesModifyRegistry::registerModifications);
    }

    private static void registerModifications(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {
        if (injectTabletPattern(id.equals(BASTION_OTHER_CHEST) || id.equals(BASTION_BRIDGE_CHEST) || id.equals(BASTION_TREASURE_CHEST) || id.equals(BASTION_HOGLIN_STABLE_CHEST), EItems.ROYAL_PATTERN_TABLET, tableBuilder)) return;
        if (injectTabletPattern(id.equals(WOODLAND_MANSION_CHEST), EItems.ARISTOCRAT_PATTERN_TABLET, tableBuilder)) return;
        if (injectTabletPattern(id.equals(DESERT_PYRAMID_CHEST), EItems.RITUAL_PATTERN_TABLET, tableBuilder)) return;
        if (injectTabletPattern(id.equals(END_CITY_TREASURE_CHEST), EItems.OCULAR_PATTERN_TABLET, tableBuilder)) return;
        if (injectTabletPattern(id.equals(SHIPWRECK_TREASURE_CHEST), EItems.HEAVENLY_PATTERN_TABLET, tableBuilder)) return;
        injectTabletPattern(id.equals(JUNGLE_TEMPLE_CHEST), EItems.ASTRONOMY_PATTERN_TABLET, tableBuilder);
    }

    private static boolean injectTabletPattern(boolean idTest, PatternTabletItem patternTablet, LootTable.Builder tableBuilder) {
        if (!idTest) return false;

        LootPool.Builder pool = LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(patternTablet).weight(5)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f))));
        tableBuilder.pool(pool);
        return true;
    }
}
