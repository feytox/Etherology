package ru.feytox.etherology.util.misc;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import ru.feytox.etherology.registry.misc.LootConditions;

import java.util.Set;

// TODO: 26.02.2024 replace with vanilla or remove todo

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomChanceWithFortuneCondition implements LootCondition {

    private final float chance;
    private final float fortuneMultiplier;

    @Override
    public LootConditionType getType() {
        return LootConditions.RANDOM_CHANCE_WITH_FORTUNE;
    }

    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack toolStack = lootContext.get(LootContextParameters.TOOL);
        int fortuneLevel = toolStack == null ? 0 : EnchantmentHelper.getLevel(Enchantments.FORTUNE, toolStack);
        return lootContext.getRandom().nextFloat() < chance + fortuneLevel * fortuneMultiplier;
    }

    public static LootCondition.Builder builder(float chance, float fortuneMultiplier) {
        return () -> new RandomChanceWithFortuneCondition(chance, fortuneMultiplier);
    }

    public static class Serializer implements JsonSerializer<RandomChanceWithFortuneCondition> {
        public void toJson(JsonObject jsonObject, RandomChanceWithFortuneCondition RandomChanceWithFortuneCondition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("chance", RandomChanceWithFortuneCondition.chance);
            jsonObject.addProperty("fortune_multiplier", RandomChanceWithFortuneCondition.fortuneMultiplier);
        }

        public RandomChanceWithFortuneCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new RandomChanceWithFortuneCondition(JsonHelper.getFloat(jsonObject, "chance"), JsonHelper.getFloat(jsonObject, "fortune_multiplier"));
        }
    }
}
