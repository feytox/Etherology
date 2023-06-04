package ru.feytox.etherology.enchantment;

import com.google.common.base.Suppliers;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.mixin.MixinEnchantmentTarget;

import java.util.function.Supplier;

public class HammerEnchantmentTarget extends MixinEnchantmentTarget {
    public static final Supplier<EnchantmentTarget> INSTANCE = Suppliers.memoize(() -> EnchantmentTarget.valueOf("HAMMER"));

    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof HammerItem;
    }
}
