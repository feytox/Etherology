package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.enchantment.EtherEnchantments;
import ru.feytox.etherology.item.BattlePickaxe;

import java.util.List;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    private boolean injectBannedEnchantments(boolean original, @Local(argsOnly = true) ItemStack stack) {
        List<Enchantment> banned = EtherEnchantments.getBannedEnchantments(stack.getItem());
        if (banned == null) return original;

        return !banned.contains((Enchantment) (Object) this) && original;
    }
}
