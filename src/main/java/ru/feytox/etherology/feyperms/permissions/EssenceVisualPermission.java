package ru.feytox.etherology.feyperms.permissions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import ru.feytox.etherology.feyperms.Permission;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class EssenceVisualPermission extends Permission {
    public EssenceVisualPermission() {
        super(new EIdentifier("essence_visual"), true);
    }

    @Override
    public boolean test(World world, PlayerEntity player) {
        Iterable<ItemStack> handStacks = player.getHandItems();

        for (ItemStack itemStack: handStacks) {
            if (itemStack.isItemEqual(Items.ACACIA_BOAT.getDefaultStack())) return true;
        }
        return false;
    }

    @Override
    public boolean apply(World world, PlayerEntity player) {
        return false;
    }
}
