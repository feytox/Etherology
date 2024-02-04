package ru.feytox.etherology.block.jewelryTable;

import io.wispforest.owo.util.ImplementedInventory;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.lense.LensPattern;
import ru.feytox.etherology.recipes.jewelry.JewelryRecipe;
import ru.feytox.etherology.recipes.jewelry.JewelryRecipeSerializer;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.registry.util.RecipesRegistry;

import java.util.List;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class JewelryTableInventory implements ImplementedInventory {

    public static final List<Integer> EMPTY_CELLS;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    @Nullable
    private final JewelryBlockEntity parent;
    @Nullable
    private Identifier currentRecipe;

    public boolean tryCraft(ServerWorld world) {
        JewelryRecipe recipe = getRecipe(world);
        if (recipe == null) return false;

        ItemStack newLens = new ItemStack(recipe.getOutputItem());
        newLens.setNbt(getStack(0).getNbt());
        val lens = EtherologyComponents.LENS.get(newLens);
        lens.setPattern(LensPattern.empty());

        setStack(0, newLens);
        markDirty();
        return true;
    }

    public boolean updateRecipe(ServerWorld world) {
        JewelryRecipe recipe = RecipesRegistry.getFirstMatch(world, this, JewelryRecipeSerializer.INSTANCE);
        if (recipe == null) currentRecipe = null;
        else currentRecipe = recipe.getId();
        return currentRecipe != null;
    }

    public boolean hasRecipe() {
        return currentRecipe != null;
    }

    @Nullable
    public JewelryRecipe getRecipe(ServerWorld world) {
        if (currentRecipe != null && RecipesRegistry.get(world, currentRecipe) instanceof JewelryRecipe recipe) {
            return recipe;
        }
        return null;
    }

    public void resetRecipe() {
        boolean updated = currentRecipe != null;
        currentRecipe = null;
        if (updated) markDirty();
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
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void markDirty() {
        ImplementedInventory.super.markDirty();
        if (parent != null) parent.trySyncData();
    }

    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        String recipeStr = currentRecipe == null ? "" : currentRecipe.toString();
        nbt.putString("recipe", recipeStr);
    }

    public void readNbt(NbtCompound nbt) {
        items.clear();
        Inventories.readNbt(nbt, items);
        String recipeStr = nbt.getString("recipe");
        currentRecipe = recipeStr.isEmpty() ? null : Identifier.tryParse(recipeStr);
    }

    static {
        EMPTY_CELLS = IntArrayList.of(0, 1, 6, 7, 8, 15, 48, 55, 56, 57, 62, 63);
    }
}