package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import ru.feytox.etherology.item.revelationView.RevelationViewItem;

import static ru.feytox.etherology.registry.item.ToolItems.register;
import static ru.feytox.etherology.registry.misc.EtherArmorMaterials.EBONY;
import static ru.feytox.etherology.registry.misc.EtherArmorMaterials.ETHRIL;

@UtilityClass
public class ArmorItems {
    // ethril armor
    public static final Item ETHRIL_HELMET = register("ethril_helmet", new ArmorItem(ETHRIL, EquipmentSlot.HEAD, new Settings()));
    public static final Item ETHRIL_CHESTPLATE = register("ethril_chestplate", new ArmorItem(ETHRIL, EquipmentSlot.CHEST, new Settings()));
    public static final Item ETHRIL_LEGGINGS = register("ethril_leggings", new ArmorItem(ETHRIL, EquipmentSlot.LEGS, new Settings()));
    public static final Item ETHRIL_BOOTS = register("ethril_boots", new ArmorItem(ETHRIL, EquipmentSlot.FEET, new Settings()));

    // ebony armor
    public static final Item EBONY_HELMET = register("ebony_helmet", new ArmorItem(EBONY, EquipmentSlot.HEAD, new Settings()));
    public static final Item EBONY_CHESTPLATE = register("ebony_chestplate", new ArmorItem(EBONY, EquipmentSlot.CHEST, new Settings()));
    public static final Item EBONY_LEGGINGS = register("ebony_leggings", new ArmorItem(EBONY, EquipmentSlot.LEGS, new Settings()));
    public static final Item EBONY_BOOTS = register("ebony_boots", new ArmorItem(EBONY, EquipmentSlot.FEET, new Settings()));

    // trinkets
    public static final Item REVELATION_VIEW = register("revelation_view", new RevelationViewItem());

    public static void registerAll() {}
}
