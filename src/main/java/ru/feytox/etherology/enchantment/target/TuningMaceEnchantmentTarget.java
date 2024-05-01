package ru.feytox.etherology.enchantment.target;

import com.google.common.base.Suppliers;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import ru.feytox.etherology.item.TuningMaceItem;
import ru.feytox.etherology.mixin.MixinEnchantmentTarget;

import java.util.function.Supplier;

public class TuningMaceEnchantmentTarget extends MixinEnchantmentTarget {
    public static final Supplier<EnchantmentTarget> INSTANCE = Suppliers.memoize(() -> EnchantmentTarget.valueOf("ETHEROLOGY_TUNING_MACE"));

    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof TuningMaceItem;
    }
}
