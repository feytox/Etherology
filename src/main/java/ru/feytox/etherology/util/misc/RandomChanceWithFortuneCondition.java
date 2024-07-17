package ru.feytox.etherology.util.misc;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import ru.feytox.etherology.registry.misc.LootConditions;

import java.util.Set;

// TODO: 26.02.2024 replace with vanilla or remove todo

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomChanceWithFortuneCondition implements LootCondition {

    public static final MapCodec<RandomChanceWithFortuneCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("chance").forGetter(condition -> condition.chance),
            Codec.FLOAT.fieldOf("fortune_multiplier").forGetter(condition -> condition.fortuneMultiplier),
            Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(condition -> condition.enchantment)
    ).apply(instance, RandomChanceWithFortuneCondition::new));

    private final float chance;
    private final float fortuneMultiplier;
    private final RegistryEntry<Enchantment> enchantment;

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

        int fortuneLevel = toolStack == null ? 0 : EnchantmentHelper.getLevel(enchantment, toolStack);
        return lootContext.getRandom().nextFloat() < chance + fortuneLevel * fortuneMultiplier;
    }

    public static LootCondition.Builder builder(RegistryWrapper.WrapperLookup registryLookup, float chance, float fortuneMultiplier) {
        return () -> new RandomChanceWithFortuneCondition(chance, fortuneMultiplier, registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE));
    }
}
