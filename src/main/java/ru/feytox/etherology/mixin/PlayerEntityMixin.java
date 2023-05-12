package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.feytox.etherology.item.HammerItem;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"))
    private float hammerEnchantDamage(ItemStack stack, EntityGroup group) {
        float damage = EnchantmentHelper.getAttackDamage(stack, group);
        if (!(stack.getItem() instanceof HammerItem)) return damage;
        PlayerEntity player = ((PlayerEntity) (Object) this);

        return player.getOffHandStack().isEmpty() ? damage : 0;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    private double hammerDamage(PlayerEntity player, EntityAttribute entityAttribute) {
        double attribute = player.getAttributeValue(entityAttribute);
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof HammerItem) || player.getOffHandStack().isEmpty()) return attribute;
        return 0;
    }
}
