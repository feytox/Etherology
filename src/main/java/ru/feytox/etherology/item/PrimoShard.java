package ru.feytox.etherology.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import ru.feytox.etherology.magic.zones.EssenceZoneType;

import java.util.List;

public class PrimoShard extends Item {

    private final String zoneId;

    public PrimoShard(EssenceZoneType zoneType) {
        super(new Item.Settings());
        this.zoneId = StringUtils.capitalize(zoneType.asString());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        Text lore = Text.translatable("lore.etherology.primoshard", zoneId).formatted(Formatting.DARK_PURPLE);
        tooltip.add(1, lore);
    }
}
