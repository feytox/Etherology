package ru.feytox.etherology.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import ru.feytox.etherology.enums.EUseAction;
import ru.feytox.etherology.util.misc.DoubleModel;
import ru.feytox.etherology.util.misc.EtherProxy;
import ru.feytox.etherology.util.misc.ItemUtils;

public class OculusItem extends Item implements DoubleModel {

    public OculusItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EUseAction.OCULUS_ETHEROLOGY.getUseAction();
    }

    public static boolean isInHands(LivingEntity entity) {
        return ItemUtils.isInHands(entity, OculusItem.class);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient) return;

        EtherProxy.getInstance().tickOculus(world, selected);
    }
}
