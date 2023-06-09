package ru.feytox.etherology.enchantment;

import com.google.common.base.Suppliers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import ru.feytox.etherology.enchantment.target.EtherShieldEnchantmentTarget;

import java.util.function.Supplier;

public class ReflectionEnchantment extends Enchantment {
    public static final Supplier<ReflectionEnchantment> INSTANCE = Suppliers.memoize(ReflectionEnchantment::new);

    private ReflectionEnchantment() {
        super(Rarity.RARE, EtherShieldEnchantmentTarget.INSTANCE.get(), new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 20;
    }
}
