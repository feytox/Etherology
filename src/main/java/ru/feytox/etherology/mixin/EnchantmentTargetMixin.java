package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.enchantment.EEnchantmentUtils;
import ru.feytox.etherology.item.BattlePickaxe;

@Mixin(targets = "net.minecraft.enchantment.EnchantmentTarget$11")
public class EnchantmentTargetMixin {

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    private boolean injectBattlePickAsWeapon(boolean original, @Local(argsOnly = true) Item item) {
        if (!(item instanceof BattlePickaxe)) return original;
        if (!EEnchantmentUtils.isBattlePickWeaponMatched()) return original;

        EnchantmentTarget it = ((EnchantmentTarget)(Object) this);
        if (it.equals(EnchantmentTarget.WEAPON)) return true;

        EEnchantmentUtils.markBattlePickNotMatched();
        Etherology.ELOGGER.error("Failed to directly inject Battle Pick into EnchantmentTarget.WEAPON (Found: {}). Using an alternative method instead", it.name());
        return original;
    }
}
