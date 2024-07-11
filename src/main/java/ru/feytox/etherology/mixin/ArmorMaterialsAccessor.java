package ru.feytox.etherology.mixin;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.EnumMap;
import java.util.function.Supplier;

@Mixin(ArmorMaterials.class)
public interface ArmorMaterialsAccessor {
    @Invoker
    static RegistryEntry<ArmorMaterial> callRegister(String id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        throw new UnsupportedOperationException();
    }
}
