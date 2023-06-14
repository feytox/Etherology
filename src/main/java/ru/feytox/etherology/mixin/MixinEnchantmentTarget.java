package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("UnusedMixin") // This mixin does not need to be added to json.
@Mixin(EnchantmentTarget.class)
public abstract class MixinEnchantmentTarget {

    @Shadow
    public abstract boolean isAcceptableItem(Item item);
}
