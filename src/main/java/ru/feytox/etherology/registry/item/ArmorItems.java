package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import ru.feytox.etherology.item.revelationView.RevelationViewItem;
import ru.feytox.etherology.mixin.ArmorMaterialsAccessor;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.EnumMap;
import java.util.function.Supplier;

import static ru.feytox.etherology.registry.item.ToolItems.register;

@UtilityClass
public class ArmorItems {

    // armor materials 2 6 5 2
    public static final RegistryEntry<ArmorMaterial> EBONY_MATERIAL = registerMaterial("ebony", Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 2);
        map.put(ArmorItem.Type.LEGGINGS, 5);
        map.put(ArmorItem.Type.CHESTPLATE, 6);
        map.put(ArmorItem.Type.HELMET, 2);
    }), 16, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0, 0, () -> Ingredient.ofItems(DecoBlockItems.EBONY_INGOT));
    public static final RegistryEntry<ArmorMaterial> ETHRIL = registerMaterial("ethril", Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 3);
        map.put(ArmorItem.Type.LEGGINGS, 6);
        map.put(ArmorItem.Type.CHESTPLATE, 8);
        map.put(ArmorItem.Type.HELMET, 3);
    }), 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.ofItems(DecoBlockItems.ETHRIL_INGOT));


    // ethril armor
    public static final Item ETHRIL_HELMET = register("ethril_helmet", new ArmorItem(ETHRIL, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final Item ETHRIL_CHESTPLATE = register("ethril_chestplate", new ArmorItem(ETHRIL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final Item ETHRIL_LEGGINGS = register("ethril_leggings", new ArmorItem(ETHRIL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final Item ETHRIL_BOOTS = register("ethril_boots", new ArmorItem(ETHRIL, ArmorItem.Type.BOOTS, new Item.Settings()));

    // ebony armor
    public static final Item EBONY_HELMET = register("ebony_helmet", new ArmorItem(EBONY_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().attributeModifiers(createEbonyAttributes())));
    public static final Item EBONY_CHESTPLATE = register("ebony_chestplate", new ArmorItem(EBONY_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().attributeModifiers(createEbonyAttributes())));
    public static final Item EBONY_LEGGINGS = register("ebony_leggings", new ArmorItem(EBONY_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().attributeModifiers(createEbonyAttributes())));
    public static final Item EBONY_BOOTS = register("ebony_boots", new ArmorItem(EBONY_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().attributeModifiers(createEbonyAttributes())));

    // trinkets
    public static final Item REVELATION_VIEW = register("revelation_view", new RevelationViewItem());

    public static void registerAll() {}

    private static RegistryEntry<ArmorMaterial> registerMaterial(String id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        return ArmorMaterialsAccessor.callRegister(EIdentifier.strId(id), defense, enchantability, equipSound, toughness, knockbackResistance, repairIngredient);
    }

    private static AttributeModifiersComponent createEbonyAttributes() {
        return AttributeModifiersComponent.builder().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier("Armor movement speed", 0.075d, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ARMOR).build();
    }
}
