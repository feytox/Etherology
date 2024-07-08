package ru.feytox.etherology.item;

import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

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
}
