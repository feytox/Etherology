package ru.feytox.etherology.registry.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import ru.feytox.etherology.registry.item.DecoBlockItems;

public enum EtherToolMaterials implements ToolMaterial {
    ETHRIL(MiningLevels.STONE, 51, 13, 1, 24,
            () -> Ingredient.ofItems(DecoBlockItems.ETHRIL_INGOT)),
    TELDER_STEEL(MiningLevels.IRON, 320, 7, 3, 16,
            () -> Ingredient.ofItems(DecoBlockItems.TELDER_STEEL_INGOT));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    EtherToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient);
    }

    public int getDurability() {
        return itemDurability;
    }

    public float getMiningSpeedMultiplier() {
        return miningSpeed;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public int getEnchantability() {
        return enchantability;
    }

    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
