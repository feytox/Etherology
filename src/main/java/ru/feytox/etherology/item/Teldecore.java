package ru.feytox.etherology.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import ru.feytox.etherology.util.misc.EtherProxy;

public class Teldecore extends Item {

    public Teldecore() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient)
            EtherProxy.getInstance().openTeldecoreScreen();

        return super.use(world, user, hand);
    }
}
