package ru.feytox.etherology.enchantment;

import com.google.common.base.Suppliers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import ru.feytox.etherology.enchantment.target.HammerEnchantmentTarget;

import java.util.function.Supplier;

public class PealEnchantment extends Enchantment {
    public static final Supplier<PealEnchantment> INSTANCE = Suppliers.memoize(PealEnchantment::new);

    private PealEnchantment() {
        super(Rarity.RARE, HammerEnchantmentTarget.INSTANCE.get(), new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
