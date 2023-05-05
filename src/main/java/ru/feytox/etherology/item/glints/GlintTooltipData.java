package ru.feytox.etherology.item.glints;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class GlintTooltipData extends BundleTooltipData {
    private final int maxEther;

    public GlintTooltipData(DefaultedList<ItemStack> inventory, int bundleOccupancy, int maxEther) {
        super(inventory, bundleOccupancy);
        this.maxEther = maxEther;
    }

    protected int getMaxEther() {
        return maxEther;
    }
}
