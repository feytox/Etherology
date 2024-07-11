package ru.feytox.etherology.mixin;

import net.minecraft.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {

    // TODO: #upd

//    @Shadow @Final private static EnumMap<ArmorItem.Type, UUID> MODIFIERS;
//
//    @Inject(method = "method_56689(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/item/ArmorItem$Type;)Lnet/minecraft/component/type/AttributeModifiersComponent;", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/AttributeModifiersComponent$Builder;build()Lnet/minecraft/component/type/AttributeModifiersComponent;"))
//    private static void inject(RegistryEntry<ArmorMaterial> material, ArmorItem.Type type, CallbackInfoReturnable<AttributeModifiersComponent> cir, @Local AttributeModifiersComponent.Builder builder) {
//        if (!material.equals(ArmorItems.EBONY)) return;
//        UUID uUID = MODIFIERS.get(type);
//        builder.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(uUID, "Armor movement speed", 0.075d, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.ARMOR);
//    }
}
