package ru.feytox.etherology.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class PrimoShard extends Item {

    private final String shardId;

    public PrimoShard(String shardId) {
        super(new Item.Settings());
        this.shardId = shardId;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        Text lore = Text.translatable("lore.etherology.primoshard", shardId).formatted(Formatting.DARK_PURPLE);
        tooltip.add(1, lore);
    }
}
