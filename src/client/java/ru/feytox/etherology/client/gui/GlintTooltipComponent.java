package ru.feytox.etherology.client.gui;

import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import ru.feytox.etherology.item.glints.GlintTooltipData;

public class GlintTooltipComponent extends BundleTooltipComponent {
    private final int maxEther;

    public GlintTooltipComponent(GlintTooltipData data) {
        super(data.component());
        maxEther = data.maxEther();
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
