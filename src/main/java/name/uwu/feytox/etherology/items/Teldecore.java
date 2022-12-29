package name.uwu.feytox.etherology.items;

import name.uwu.feytox.etherology.gui.teldecore.TestScreen;
import name.uwu.feytox.etherology.util.SimpleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static name.uwu.feytox.etherology.EtherologyComponents.*;

public class Teldecore extends SimpleItem {

    public Teldecore() {
        super("teldecore", getSettings());
    }

    private static Item.Settings getSettings() {
        return new FabricItemSettings().group(ItemGroup.MISC).maxCount(1);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
//            EtherologyClient.chapters.current().open();
            MinecraftClient.getInstance().setScreen(new TestScreen());
        } else {
            float foobar = user.getComponent(ETHER_POINTS).getValue();
            float foo = user.getComponent(ETHER_MAX).getValue();
            float bar = user.getComponent(ETHER_REGEN).getValue();
            user.sendMessage(Text.of("ETHER_POINTS: " + foobar));
            user.sendMessage(Text.of("ETHER_MAX: " + foo));
            user.sendMessage(Text.of("ETHER_REGEN: " + bar));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
