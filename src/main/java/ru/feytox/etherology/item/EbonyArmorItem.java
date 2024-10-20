package ru.feytox.etherology.item;

import com.google.common.base.Suppliers;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.mixin.ArmorItemAccessor;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.function.Supplier;

public class EbonyArmorItem extends ArmorItem {

    public static final Identifier EBONY_SPEED_ID = EIdentifier.of("ebony_speed");
    private final Supplier<AttributeModifiersComponent> modifiers;

    public EbonyArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
        super(material, type, settings);

        this.modifiers = Suppliers.memoize(() -> ((ArmorItemAccessor) this).getModifiersSupplier().get()
                .with(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(EBONY_SPEED_ID, 0.075d, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        AttributeModifierSlot.forEquipmentSlot(type.getEquipmentSlot())));
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return modifiers.get();
    }
}
