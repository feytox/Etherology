package ru.feytox.etherology.registry.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.Getter;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import ru.feytox.etherology.registry.item.DecoBlockItems;

@SuppressWarnings("Guava")
public enum EtherToolMaterials implements ToolMaterial {
    ETHRIL(MiningLevels.DIAMOND, 1561, 8.0F, 3.0F, 10,
            () -> Ingredient.ofItems(DecoBlockItems.ETHRIL_INGOT)),
    EBONY(MiningLevels.IRON, 320, 7, 3, 16,
            () -> Ingredient.ofItems(DecoBlockItems.EBONY_INGOT));

    @Getter
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    @Getter
    private final float attackDamage;
    @Getter
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

    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
