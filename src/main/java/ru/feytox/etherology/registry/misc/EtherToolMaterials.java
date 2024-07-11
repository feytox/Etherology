package ru.feytox.etherology.registry.misc;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import ru.feytox.etherology.registry.item.DecoBlockItems;

@SuppressWarnings("Guava")
public enum EtherToolMaterials implements ToolMaterial {
    ETHRIL(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 3.0F, 10,
            () -> Ingredient.ofItems(DecoBlockItems.ETHRIL_INGOT)),
    EBONY(BlockTags.INCORRECT_FOR_IRON_TOOL, 320, 7, 3, 16,
            () -> Ingredient.ofItems(DecoBlockItems.EBONY_INGOT));

    @Getter
    private final TagKey<Block> inverseTag;
    private final int itemDurability;
    private final float miningSpeed;
    @Getter
    private final float attackDamage;
    @Getter
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    EtherToolMaterials(TagKey<Block> inverseTag, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.inverseTag = inverseTag;
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
