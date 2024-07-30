package ru.feytox.etherology.item;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;

public class Teldecore extends Item {

    public Teldecore() {
        super(new Settings());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) return super.use(world, user, hand);

        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new TeldecoreScreen(client.currentScreen));

        return super.use(world, user, hand);
    }
}
