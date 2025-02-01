package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import ru.feytox.etherology.item.EbonyArmorItem;
import ru.feytox.etherology.item.RevelationViewItem;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.item.ArmorItem.Type.*;
import static ru.feytox.etherology.registry.item.ToolItems.register;

@UtilityClass
public class ArmorItems {

    // materials
    public static final RegistryEntry<ArmorMaterial> EBONY_MATERIAL = registerMaterial("ebony", Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(HELMET, 2);
    }), 16, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0, 0, () -> Ingredient.ofItems(DecoBlockItems.EBONY_INGOT));

    // ebony armor
    public static final Item EBONY_HELMET = register("ebony_helmet", createArmor(EbonyArmorItem::new, EBONY_MATERIAL, HELMET));
    public static final Item EBONY_CHESTPLATE = register("ebony_chestplate", createArmor(EbonyArmorItem::new, EBONY_MATERIAL, CHESTPLATE));
    public static final Item EBONY_LEGGINGS = register("ebony_leggings", createArmor(EbonyArmorItem::new, EBONY_MATERIAL, LEGGINGS));
    public static final Item EBONY_BOOTS = register("ebony_boots", createArmor(EbonyArmorItem::new, EBONY_MATERIAL, BOOTS));

    public static final Item[] ARMOR_ITEMS = {EBONY_HELMET, EBONY_CHESTPLATE, EBONY_LEGGINGS, EBONY_BOOTS};

    // trinkets
    public static final Item REVELATION_VIEW = register("revelation_view", new RevelationViewItem());

    public static void registerAll() {}

    private static RegistryEntry<ArmorMaterial> registerMaterial(String id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, EIdentifier.of(id), new ArmorMaterial(defense, enchantability, equipSound, repairIngredient, List.of(new ArmorMaterial.Layer(EIdentifier.of(id))), toughness, knockbackResistance));
    }

    private static ArmorItem createArmor(ArmorFactory factory, RegistryEntry<ArmorMaterial> material, ArmorItem.Type armorType) {
        return factory.create(material, armorType, createArmorSettings(armorType));
    }

    private static Item.Settings createArmorSettings(ArmorItem.Type armorType) {
        return new Item.Settings().maxDamage(armorType.getMaxDamage(15));
    }

    @FunctionalInterface
    private interface ArmorFactory {

        ArmorItem create(RegistryEntry<ArmorMaterial> material, ArmorItem.Type type, Item.Settings settings);
    }
}
