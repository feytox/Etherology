package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.item.BroadSwordItem;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyReturnValue(method = "getSweepingMultiplier(Lnet/minecraft/entity/LivingEntity;)F", at = @At("RETURN"))
    private static float getBroadSwordSweeping(float original, @Local(argsOnly = true) LivingEntity entity) {
        return BroadSwordItem.getBroadSwordSweeping(original, entity);
    }
}
