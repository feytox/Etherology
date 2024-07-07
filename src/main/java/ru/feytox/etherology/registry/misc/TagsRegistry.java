package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.TagExcludeUtil;

import static net.minecraft.registry.tag.ItemTags.*;

@UtilityClass
public class TagsRegistry {

    public static final TagKey<Item> PEACH_LOGS = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("peach_logs"));
    public static final TagKey<Item> TUNING_MACES = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("tuning_maces"));
    public static final TagKey<Item> ETHER_SHIELDS = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("ether_shields"));

    public static void registerAll() {
        excludeTags();
    }

    private static void excludeTags() {
        TagExcludeUtil.exclude(MINING_LOOT_ENCHANTABLE, ToolItems.BATTLE_PICKAXES);

    }
}
