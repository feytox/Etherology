package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnchantmentTarget.class)
public abstract class MixinEnchantmentTarget {

    @Shadow
    public abstract boolean isAcceptableItem(Item item);
}
