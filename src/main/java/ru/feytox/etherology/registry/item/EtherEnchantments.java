package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.function.Supplier;

@UtilityClass
public class EtherEnchantments {

    public static void registerAll() {
        register("peal", PealEnchantment.INSTANCE);
    }

    private void register(String id, Supplier<? extends Enchantment> enchantSupplier) {
        Registry.register(Registries.ENCHANTMENT, new EIdentifier(id), enchantSupplier.get());
    }
}
