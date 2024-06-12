package ru.feytox.etherology.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import ru.feytox.etherology.gui.teldecore.TestScreen;
import ru.feytox.etherology.util.deprecated.SimpleItem;

public class Teldecore extends SimpleItem {

    public Teldecore() {
        super("teldecore");
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
//            EtherologyClient.chapters.current().open();
            MinecraftClient.getInstance().setScreen(new TestScreen());
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
