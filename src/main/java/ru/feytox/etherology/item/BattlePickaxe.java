package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.entry.RegistryEntry;

public class BattlePickaxe extends PickaxeItem {

    private final float attackDamage;

    public BattlePickaxe(ToolMaterial material, int attackDamage, float attackSpeed) {
        this(material, attackDamage, attackSpeed, new Settings());
    }

    public BattlePickaxe(ToolMaterial material, int attackDamage, float attackSpeed, Settings itemSettings) {
        super(material, itemSettings.attributeModifiers(PickaxeItem.createAttributeModifiers(material, attackDamage, attackSpeed)));
        this.attackDamage = attackDamage + material.getAttackDamage();
    }

    public float getDamagePercent() {
        return (attackDamage + 1) / 7;
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        if (!super.canBeEnchantedWith(stack, enchantment, context)) return false;
        return !enchantment.matchesKey(Enchantments.FORTUNE) && !enchantment.matchesKey(Enchantments.SILK_TOUCH);
    }
}
