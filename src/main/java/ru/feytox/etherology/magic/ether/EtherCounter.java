package ru.feytox.etherology.magic.ether;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static ru.feytox.etherology.registry.item.EItems.ETHER_SHARD;

public interface EtherCounter {
    float getEtherCount();
    List<Integer> getCounterSlots();
    Inventory getInventoryForCounter();
    void tickEtherCount(ServerWorld world);

    default void updateCount() {
        int etherValue = MathHelper.floor(getEtherCount());
        List<Integer> slots = getCounterSlots();
        Inventory inv = getInventoryForCounter();

        for (Integer i: slots) {
            ItemStack stack = ItemStack.EMPTY;
            int count = Math.min(64, etherValue);
            etherValue -= count;
            if (count > 0) {
                stack = ETHER_SHARD.getDefaultStack();
                stack.setCount(count);
            }
            inv.setStack(i, stack);
        }
    }
}
