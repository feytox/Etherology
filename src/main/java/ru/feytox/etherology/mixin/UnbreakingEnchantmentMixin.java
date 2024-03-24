package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.enchantment.EEnchantmentUtils;

import java.util.List;

@Mixin(UnbreakingEnchantment.class)
public class UnbreakingEnchantmentMixin {

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    private boolean disallowUnbreaking(boolean original, @Local(argsOnly = true) ItemStack stack) {
        List<Enchantment> banned = EEnchantmentUtils.getBannedEnchantments(stack.getItem());
        if (banned == null) return original;

        Enchantment it = ((Enchantment) (Object) this);
        return !banned.contains(it) && original;
    }
}
