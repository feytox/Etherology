package name.uwu.feytox.etherology.util;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class UnspecificSlot extends Slot {
    private final List<ItemStack> blocklist;

    public UnspecificSlot(Inventory inventory, List<Item> blockedItems, int index, int x, int y) {
        super(inventory, index, x, y);
        this.blocklist = Lists.transform(blockedItems, Item::getDefaultStack);
    }

    public UnspecificSlot(Inventory inventory, Item blockedItem, int index, int x, int y) {
        super(inventory, index, x, y);
        this.blocklist = new ArrayList<>(List.of(blockedItem.getDefaultStack()));
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        for (ItemStack itemStack: blocklist) {
            if (stack.isItemEqual(itemStack)) return false;
        }
        return true;
    }
}
