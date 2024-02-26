package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.JsonSerializer;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.RandomChanceWithFortuneCondition;

@UtilityClass
public class LootConditions {

    public static final LootConditionType RANDOM_CHANCE_WITH_FORTUNE = register("random_chance_with_fortune", new RandomChanceWithFortuneCondition.Serializer());

    public static void registerAll() {}

    private static LootConditionType register(String id, JsonSerializer<? extends LootCondition> serializer) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, new EIdentifier(id), new LootConditionType(serializer));
    }
}
