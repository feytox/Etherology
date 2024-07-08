package ru.feytox.etherology.item.glints;

import net.minecraft.client.item.TooltipData;
import net.minecraft.component.type.BundleContentsComponent;

public record GlintTooltipData(BundleContentsComponent component, int maxEther) implements TooltipData {

}
