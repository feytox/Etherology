package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.UseAction;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.util.misc.DoubleModel;

public class BroadSwordItem extends TwoHandheldSword implements DoubleModel {

    public BroadSwordItem() {
        super(ToolMaterials.IRON, 5, -3.1f, new FabricItemSettings());
    }

    public static boolean checkBroadSword(PlayerEntity player) {
        return check(player, BroadSwordItem.class);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.TWOHANDHELD_ETHEROLOGY.getUseAction();
    }
}
