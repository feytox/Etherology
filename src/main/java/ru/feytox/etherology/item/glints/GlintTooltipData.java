package ru.feytox.etherology.item.glints;

import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.tooltip.TooltipData;

public record GlintTooltipData(BundleContentsComponent component, int maxEther) implements TooltipData {

}
