package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import ru.feytox.etherology.effect.EtherEffect;
import ru.feytox.etherology.registry.item.DecoBlockItems;
import ru.feytox.etherology.util.misc.EIdentifier;

import static net.minecraft.entity.effect.StatusEffectCategory.BENEFICIAL;
import static net.minecraft.entity.effect.StatusEffectCategory.HARMFUL;

@UtilityClass
public class EffectsRegistry {

    public static final RegistryEntry<StatusEffect> DEVASTATION = register("devastation", new EtherEffect(HARMFUL, 0x5A5672, -0.1f));
    public static final RegistryEntry<StatusEffect> VITAL_ENERGY = register("vital_energy", new EtherEffect(BENEFICIAL, 0x59FFD5, 0.1f));

    public static final RegistryEntry<Potion> VITAL_ENERGY_POTION = registerPotion("vital_energy", new StatusEffectInstance(VITAL_ENERGY, 900));
    public static final RegistryEntry<Potion> STRONG_VITAL_ENERGY_POTION = registerPotion("strong_vital_energy", "vital_energy", new StatusEffectInstance(VITAL_ENERGY, 450, 1));
    public static final RegistryEntry<Potion> LONG_VITAL_ENERGY_POTION = registerPotion("long_vital_energy", "vital_energy", new StatusEffectInstance(VITAL_ENERGY, 1800));

    public static void registerAll() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, DecoBlockItems.BEAM_FRUIT, VITAL_ENERGY_POTION);
            builder.registerPotionRecipe(VITAL_ENERGY_POTION, Items.REDSTONE, LONG_VITAL_ENERGY_POTION);
            builder.registerPotionRecipe(VITAL_ENERGY_POTION, Items.GLOWSTONE_DUST, STRONG_VITAL_ENERGY_POTION);
        });
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, EIdentifier.of(id), effect);
    }

    private static RegistryEntry<Potion> registerPotion(String id, StatusEffectInstance effectInstance) {
        return Registry.registerReference(Registries.POTION, EIdentifier.of(id), new Potion(effectInstance));
    }

    private static RegistryEntry<Potion> registerPotion(String id, String baseName, StatusEffectInstance effectInstance) {
        return Registry.registerReference(Registries.POTION, EIdentifier.of(id), new Potion(baseName, effectInstance));
    }
}
