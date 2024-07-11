package ru.feytox.etherology.registry.misc;

import com.mojang.serialization.MapCodec;
import lombok.experimental.UtilityClass;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RandomChanceWithFortuneCondition;

@UtilityClass
public class LootConditions {

    public static final LootConditionType RANDOM_CHANCE_WITH_FORTUNE = register("random_chance_with_fortune", RandomChanceWithFortuneCondition.CODEC);

    public static void registerAll() {}

    private static <T extends LootCondition> LootConditionType register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, EIdentifier.of(id), new LootConditionType(codec));
    }
}
