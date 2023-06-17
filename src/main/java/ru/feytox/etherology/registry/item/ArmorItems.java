package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

import static ru.feytox.etherology.registry.item.ToolItems.register;
import static ru.feytox.etherology.registry.util.EtherArmorMaterials.ETHRIL;
import static ru.feytox.etherology.registry.util.EtherArmorMaterials.TELDER_STEEL;

@UtilityClass
public class ArmorItems {
    // ethril armor
    public static final Item ETHRIL_HELMET = register("ethril_helmet", new ArmorItem(ETHRIL, EquipmentSlot.HEAD, new FabricItemSettings()));
    public static final Item ETHRIL_CHESTPLATE = register("ethril_chestplate", new ArmorItem(ETHRIL, EquipmentSlot.CHEST, new FabricItemSettings()));
    public static final Item ETHRIL_LEGGINGS = register("ethril_leggings", new ArmorItem(ETHRIL, EquipmentSlot.LEGS, new FabricItemSettings()));
    public static final Item ETHRIL_BOOTS = register("ethril_boots", new ArmorItem(ETHRIL, EquipmentSlot.FEET, new FabricItemSettings()));

    // telder_steel armor
    public static final Item TELDER_STEEL_HELMET = register("telder_steel_helmet", new ArmorItem(TELDER_STEEL, EquipmentSlot.HEAD, new FabricItemSettings()));
    public static final Item TELDER_STEEL_CHESTPLATE = register("telder_steel_chestplate", new ArmorItem(TELDER_STEEL, EquipmentSlot.CHEST, new FabricItemSettings()));
    public static final Item TELDER_STEEL_LEGGINGS = register("telder_steel_leggings", new ArmorItem(TELDER_STEEL, EquipmentSlot.LEGS, new FabricItemSettings()));
    public static final Item TELDER_STEEL_BOOTS = register("telder_steel_boots", new ArmorItem(TELDER_STEEL, EquipmentSlot.FEET, new FabricItemSettings()));

    public static void registerAll() {}
}
