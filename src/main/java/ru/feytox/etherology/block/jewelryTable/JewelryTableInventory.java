package ru.feytox.etherology.block.jewelryTable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.lense.LensPattern;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.util.feyapi.UpdatableInventory;

import java.util.List;

public class JewelryTableInventory implements UpdatableInventory {

    public static final List<Integer> EMPTY_CELLS;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {
        // TODO: 21.01.2024 implement?
    }

    @Override
    public void onTrackedUpdate(int index) {
        // TODO: 21.01.2024 implement?
    }

    public void updateCells(int crackPos) {
        ItemStack lens = getStack(0);
        val lensData = EtherologyComponents.LENS.get(lens);
        LensPattern pattern = lensData.getPattern();
        int x0 = crackPos & 0b111;
        int y0 = (crackPos >> 3) & 0b111;

        // TODO: 23.01.2024 simplify???
        // -->
        for (int x = x0+1; x < 8; x++) {
            if (lens.isEmpty()) return;
            if (scanCell(x, y0, x0, y0, pattern)) continue;
            break;
        }

        // <--
        for (int x = x0-1; x >= 0; x--) {
            if (lens.isEmpty()) return;
            if (scanCell(x, y0, x0, y0, pattern)) continue;
            break;
        }

        // down
        for (int y = y0+1; y < 8; y++) {
            if (lens.isEmpty()) return;
            if (scanCell(x0, y, x0, y0, pattern)) continue;
            break;
        }

        // up
        for (int y = y0-1; y >= 0; y--) {
            if (lens.isEmpty()) return;
            if (scanCell(x0, y, x0, y0, pattern)) continue;
            break;
        }

        lensData.setPattern(pattern);
    }

    private boolean scanCell(int x, int y, int x0, int y0, LensPattern pattern) {
        int cell = (y << 3) | x;
        if (EMPTY_CELLS.contains(cell)) return false;

        boolean isSoft = pattern.isSoft(cell);
        boolean isHard = !isSoft && pattern.isHard(cell);
        if (!isSoft && !isHard) return true;

        int damage = 2;
        if (isHard) damage = 10;
        damageLens(damage);

        // TODO: 23.01.2024 durability decrement
        fill(pattern, x0, y0, x, y);
        return false;
    }

    private void fill(LensPattern pattern, int x0, int y0, int x1, int y1) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++) {
            for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) {
                int cell = (y << 3) | x;
                if (EMPTY_CELLS.contains(cell)) continue;

                pattern.unSoft(cell);
                pattern.markHard(cell);
            }
        }
    }

    public boolean markCell(boolean isSoft, int index) {
        val lensData = EtherologyComponents.LENS.get(getStack(0));
        LensPattern pattern = lensData.getPattern();

        boolean result = isSoft ? pattern.markSoft(index) : pattern.markHard(index);
        if (!result) return false;

        lensData.setPattern(pattern);
        return true;
    }

    public boolean damageLens(int amount) {
        ItemStack lens = getStack(0);
        if (!lens.damage(amount, Random.create(), null)) return false;

        lens.decrement(1);
        markDirty();
        return true;
    }

    public int getTextureOffset(int index) {
        val lensData = EtherologyComponents.LENS.get(items.get(0));
        LensPattern pattern = lensData.getPattern();
        return pattern.getTextureOffset(index);
    }

    @Override
    public void onSpecialEvent(int eventId, ItemStack stack) {}

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    static {
        EMPTY_CELLS = IntArrayList.of(0, 1, 6, 7, 8, 15, 48, 55, 56, 57, 62, 63);
    }
}
