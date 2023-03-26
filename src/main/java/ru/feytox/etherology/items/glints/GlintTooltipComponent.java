package ru.feytox.etherology.items.glints;

import net.minecraft.client.gui.tooltip.BundleTooltipComponent;

public class GlintTooltipComponent extends BundleTooltipComponent {
    private final int maxEther;

    public GlintTooltipComponent(GlintTooltipData data) {
        super(data);
        maxEther = data.getMaxEther();
    }

    @Override
    public int getColumns() {
        return maxEther <= 64 ? 1 : maxEther <= 128 ? 2 : super.getColumns();
    }

    @Override
    public int getRows() {
        return maxEther <= 128 ? 1 : super.getRows();
    }
}
