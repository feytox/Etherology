package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.enchantment.EtherEnchantments;
import ru.feytox.etherology.item.BattlePickaxe;
import ru.feytox.etherology.item.BroadSwordItem;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyExpressionValue(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean alternativeInjectBattlePickToWeapon(boolean original, @Local Enchantment enchantment, @Local Item item) {
        if (EtherEnchantments.isBattlePickWeaponMatched()) return original;
        return original || (enchantment.type.equals(EnchantmentTarget.WEAPON) && item instanceof BattlePickaxe);
    }

    @ModifyExpressionValue(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean injectBannedEnchantments(boolean original, @Local Enchantment enchantment, @Local Item item) {
        List<Enchantment> banned = EtherEnchantments.getBannedEnchantments(item);
        if (banned == null) return original;

        return !banned.contains(enchantment) && original;
    }

    @ModifyReturnValue(method = "getSweepingMultiplier", at = @At("RETURN"))
    private static float getBroadSwordSweeping(float original, @Local(argsOnly = true) LivingEntity entity) {
        return BroadSwordItem.getBroadSwordSweeping(original, entity);
    }
}
