package ru.feytox.etherology.mixin;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {

    @Accessor("attributeModifiers")
    Supplier<AttributeModifiersComponent> getModifiersSupplier();
}
