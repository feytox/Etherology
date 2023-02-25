package name.uwu.feytox.etherology.feyperms.permissions;

import name.uwu.feytox.etherology.feyperms.Permission;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class EssenceVisualPermission extends Permission {
    public EssenceVisualPermission() {
        super("essence_visual", true);
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
