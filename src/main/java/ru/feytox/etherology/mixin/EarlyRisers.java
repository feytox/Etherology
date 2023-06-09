package ru.feytox.etherology.mixin;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class EarlyRisers implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String enchantmentTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("HAMMER", "ru.feytox.etherology.enchantment.target.HammerEnchantmentTarget").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ETHEROLOGY_SHIELD", "ru.feytox.etherology.enchantment.target.EtherShieldEnchantmentTarget").build();
    }
}
