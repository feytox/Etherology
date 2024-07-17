package ru.feytox.etherology.data;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class EBlockTags {

    public static final TagKey<Block> PEACH_LOGS = TagKey.of(RegistryKeys.BLOCK, EIdentifier.of("peach_logs"));
}
