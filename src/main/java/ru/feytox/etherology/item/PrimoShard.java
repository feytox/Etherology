package ru.feytox.etherology.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PrimoShard extends Item {

    private final String shardId;

    public PrimoShard(String shardId) {
        super(new Item.Settings());
        this.shardId = shardId;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Text lore = Text.translatable("lore.etherology.primoshard", shardId).formatted(Formatting.DARK_PURPLE);
        tooltip.add(1, lore);
    }
}
