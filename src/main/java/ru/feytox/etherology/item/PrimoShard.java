package ru.feytox.etherology.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.deprecated.SimpleItem;

import java.util.List;

public class PrimoShard extends SimpleItem {

    private PrimoShard(String itemId) {
        super(itemId);
    }

    public String getShardName() {
        return itemId.split("_")[1];
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        String shardName = getShardName();
        shardName = shardName.substring(0, 1).toUpperCase() + shardName.substring(1);
        Text lore = Text.translatable("lore.etherology.primoshard", shardName).formatted(Formatting.DARK_PURPLE);
        tooltip.add(1, lore);
    }

    public static class KetaPrimoShard extends PrimoShard {
        public KetaPrimoShard() {
            super("primoshard_keta");
        }
    }
    public static class RellaPrimoShard extends PrimoShard {
        public RellaPrimoShard() {
            super("primoshard_rella");
        }
    }
    public static class ClosPrimoShard extends PrimoShard {
        public ClosPrimoShard() {
            super("primoshard_clos");
        }
    }
    public static class ViaPrimoShard extends PrimoShard {
        public ViaPrimoShard() {
            super("primoshard_via");
        }
    }
}
