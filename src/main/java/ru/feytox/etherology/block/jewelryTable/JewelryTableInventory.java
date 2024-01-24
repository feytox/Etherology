package ru.feytox.etherology.block.jewelryTable;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import lombok.val;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.magic.lense.LensPattern;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.util.feyapi.UpdatableInventory;

public class JewelryTableInventory implements UpdatableInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {
        // TODO: 21.01.2024 implement?
    }

    @Override
    public void onTrackedUpdate(int index) {
        // TODO: 21.01.2024 implement?
    }

    public void updateCells(int crackIndex) {
        val lensData = EtherologyComponents.LENS.get(items.get(0));
        LensPattern pattern = lensData.getPattern();

        // TODO: 23.01.2024 simplify???
        Pair<Integer, Integer> pos = getCellPos(crackIndex);
        // -->
        for (int x = pos.left()+1; x < 8; x++) {
            if (scanCell(x, pos.right(), pos, pattern)) continue;
            break;
        }

        // <--
        for (int x = pos.left()-1; x >= 0; x--) {
            if (scanCell(x, pos.right(), pos, pattern)) continue;
            break;
        }

        // down
        for (int y = pos.right()+1; y < 8; y++) {
            if (scanCell(pos.left(), y, pos, pattern)) continue;
            break;
        }

        // up
        for (int y = pos.right()-1; y >= 0; y--) {
            if (scanCell(pos.left(), y, pos, pattern)) continue;
            break;
        }

        lensData.setPattern(pattern);
    }

    private boolean scanCell(int x, int y, Pair<Integer, Integer> pos, LensPattern pattern) {
        Integer cell = getCell(x, y);
        if (cell == null) return false;
        boolean isSoft = pattern.isSoft(cell);
        boolean isHard = !isSoft && pattern.isHard(cell);
        if (!isSoft && !isHard) return true;

        // TODO: 23.01.2024 durability decrement
        fill(pattern, pos.left(), pos.right(), x, y);
        return false;
    }

    private void fill(LensPattern pattern, int x0, int y0, int x1, int y1) {
        for (int x = Math.min(x0, x1); x <= Math.max(x0, x1); x++) {
            for (int y = Math.min(y0, y1); y <= Math.max(y0, y1); y++) {
                Integer cell = getCell(x, y);
                if (cell == null) continue;

                Etherology.ELOGGER.info("{} {} - {}", x, y, cell);
                pattern.unSoft(cell);
                pattern.markHard(cell);
            }
        }
    }

    private Pair<Integer, Integer> getCellPos(int index) {
        if (index < 4) return new IntIntImmutablePair(2 + index, 0);
        if (index < 10) return new IntIntImmutablePair(1 + index - 4, 1);
        if (index < 42) return new IntIntImmutablePair((index - 10) % 8, 2 + (index - 10) / 8);
        if (index < 48) return new IntIntImmutablePair(1 + index - 42, 6);
        return new IntIntImmutablePair(2 + index - 48, 7);
    }

    private Integer getCell(int x, int y) {
        int result = getCellIndex(x, y);
        return result < 0 ? null : result;
    }

    private int getCellIndex(int x, int y) {
        if (x < 0 || y < 0 || x > 7 || y > 7) return -1;
        if (y > 1 & y < 6) return 8*y + x - 6;
        if (y == 0) return x - 2;
        if (y == 1) return x + 3;
        if (y == 6) return x + 41;
        return x + 46;
    }

    public boolean markCell(boolean isSoft, int index) {
        val lensData = EtherologyComponents.LENS.get(items.get(0));
        LensPattern pattern = lensData.getPattern();

        boolean result = isSoft ? pattern.markSoft(index) : pattern.markHard(index);
        if (!result) return false;

        lensData.setPattern(pattern);
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
}
