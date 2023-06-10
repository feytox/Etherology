package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class BattlePickaxe extends PickaxeItem {
    public BattlePickaxe(ToolMaterial material, int attackDamage, float attackSpeed) {
        this(material, attackDamage, attackSpeed, new FabricItemSettings());
    }

    public BattlePickaxe(ToolMaterial material, int attackDamage, float attackSpeed, FabricItemSettings itemSettings) {
        super(material, attackDamage, attackSpeed, itemSettings);
    }

    public float getDamagePercent() {
        return (getAttackDamage() + 1) / 7;
    }
}
