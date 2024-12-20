package ru.feytox.etherology.data;

import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EItemTags {

    public static final TagKey<Item> PEACH_LOGS = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("peach_logs"));
    public static final TagKey<Item> TUNING_MACES = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("tuning_maces"));
    public static final TagKey<Item> IRON_SHIELDS = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("iron_shields"));
    public static final TagKey<Item> SEDIMENTARY_STONES = TagKey.of(RegistryKeys.ITEM, EIdentifier.of("sedimentary_stones"));
}
