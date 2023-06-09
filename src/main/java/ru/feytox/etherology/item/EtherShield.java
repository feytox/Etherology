package ru.feytox.etherology.item;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import net.minecraft.item.Item;

public class EtherShield extends FabricShieldItem {
    public EtherShield(Settings settings, int coolDownTicks, int enchantability, Item... repairItems) {
        super(settings, coolDownTicks, enchantability, repairItems);
    }
}
