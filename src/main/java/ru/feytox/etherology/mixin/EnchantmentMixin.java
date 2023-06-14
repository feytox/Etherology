package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.LuckEnchantment;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.item.BattlePickaxe;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    
    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void battlePickTargetExtend(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment it = ((Enchantment) (Object) this);
        if (!(stack.getItem() instanceof BattlePickaxe)) return;
        if (it.type.equals(EnchantmentTarget.WEAPON)) cir.setReturnValue(true);
        if (it.type.equals(EnchantmentTarget.DIGGER) && (it instanceof LuckEnchantment || it instanceof SilkTouchEnchantment)) cir.setReturnValue(false);
    }
}
