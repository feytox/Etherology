package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.enchantment.EtherEnchantments;
import ru.feytox.etherology.item.BattlePickaxe;

import java.util.List;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    private boolean battlePickTargetExtend2(boolean original, @Local(argsOnly = true) ItemStack stack) {
        Enchantment it = ((Enchantment) (Object) this);

        if (!(stack.getItem() instanceof BattlePickaxe)) return original;
        return it.type.equals(EnchantmentTarget.WEAPON) || original;
    }

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    private boolean injectBannedEnchantments(boolean original, @Local(argsOnly = true) ItemStack stack) {
        List<Enchantment> banned = EtherEnchantments.getBannedEnchantments(stack.getItem());
        if (banned == null) return original;

        Enchantment it = ((Enchantment) (Object) this);
        return !banned.contains(it) && original;
    }
}
