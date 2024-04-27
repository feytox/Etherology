package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.UseAction;
import ru.feytox.etherology.enums.EArmPose;

public class TuningMaceItem extends TwoHandheldSword {

    public TuningMaceItem() {
        super(ToolMaterials.IRON, 9, -3.6f, new FabricItemSettings());
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.TUNING_MACE_ETHEROLOGY.getUseAction();
    }
}
