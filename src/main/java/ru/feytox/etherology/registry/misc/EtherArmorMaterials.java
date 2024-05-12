package ru.feytox.etherology.registry.misc;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.Getter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import ru.feytox.etherology.registry.item.DecoBlockItems;

@SuppressWarnings("Guava")
public enum EtherArmorMaterials implements ArmorMaterial {
    EBONY("ebony", 18, new int[]{2, 6, 5, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0, 0, () -> Ingredient.ofItems(DecoBlockItems.EBONY_INGOT)),
    ETHRIL("ethril", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.ofItems(DecoBlockItems.ETHRIL_INGOT));

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    @Getter
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    @Getter
    private final int enchantability;
    @Getter
    private final SoundEvent equipSound;
    @Getter
    private final float toughness;
    @Getter
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredientSupplier;

    EtherArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredientSupplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredientSupplier = Suppliers.memoize(repairIngredientSupplier);
    }

    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    public int getProtectionAmount(EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredientSupplier.get();
    }

}
