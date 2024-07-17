package ru.feytox.etherology.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.feytox.etherology.block.spill_barrel.SpillBarrelBlockEntity;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;

public class SpillBarrelItem extends BlockItem {
    public SpillBarrelItem() {
        super(EBlocks.SPILL_BARREL, new Settings());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        ContainerComponent barrelData = stack.get(DataComponentTypes.CONTAINER);
        ItemStack potionStack = barrelData != null ? barrelData.copyFirstStack() : null;
        long potionCount = barrelData != null ? barrelData.streamNonEmpty().count() : 0;
        MutableText potionInfo = potionStack == null ? null : SpillBarrelBlockEntity.getPotionInfo(potionStack, potionCount, false, Text.empty());

        if (potionCount == 0 || potionInfo == null) {
            tooltip.add(1, Text.translatable("lore.etherology.spill_barrel.empty").formatted(Formatting.GRAY));
            return;
        }

        tooltip.add(1, potionInfo.formatted(Formatting.GRAY));
    }
}
