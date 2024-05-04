package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.UseAction;
import ru.feytox.etherology.enums.EArmPose;

public class TuningMaceItem extends TwoHandheldSword {

    public TuningMaceItem() {
        super(ToolMaterials.IRON, 6, -3.2f, new FabricItemSettings().maxDamage(476));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.TWOHANDHELD_ETHEROLOGY.getUseAction();
    }
}
