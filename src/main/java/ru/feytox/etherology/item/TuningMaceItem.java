package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.UseAction;
import ru.feytox.etherology.enums.EUseAction;

public class TuningMaceItem extends TwoHandheldSword {

    public TuningMaceItem() {
        super(ToolMaterials.IRON, 6, -3.2f, new Settings().maxDamage(476));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EUseAction.TWOHANDHELD_ETHEROLOGY.getUseAction();
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        if (!super.canBeEnchantedWith(stack, enchantment, context)) return false;
        return !enchantment.matchesKey(Enchantments.SHARPNESS) && !enchantment.matchesKey(Enchantments.FIRE_ASPECT)
                && !enchantment.matchesKey(Enchantments.LOOTING) && !enchantment.matchesKey(Enchantments.SWEEPING_EDGE);
    }
}
