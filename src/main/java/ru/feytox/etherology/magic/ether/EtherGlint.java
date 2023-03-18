package ru.feytox.etherology.magic.ether;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import ru.feytox.etherology.items.glints.AbstractGlintItem;

public class EtherGlint {
    private final ItemStack stack;
    private final float maxEther;

    public EtherGlint(ItemStack glintStack) {
        this.stack = glintStack;
        if (glintStack.getItem() instanceof AbstractGlintItem glintItem) {
            this.maxEther = glintItem.getMaxEther();
        } else {
            this.maxEther = 0;
        }
    }

    public boolean isFull() {
        return getStoredEther() == maxEther;
    }

    /**
     * @return излишек, который не поместился в глинт
     */
    public float increment(float value) {
        return AbstractGlintItem.increment(stack, maxEther, value);
    }

    /**
     * @return количество забранного эфира
     */
    public float decrement(float value) {
        return AbstractGlintItem.decrement(stack, maxEther, value);
    }

    public float getMaxEther() {
        return maxEther;
    }

    public float getStoredEther() {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return 0;
        return nbt.getFloat("stored_ether");
    }
}
