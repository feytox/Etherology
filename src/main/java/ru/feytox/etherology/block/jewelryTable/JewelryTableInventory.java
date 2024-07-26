package ru.feytox.etherology.block.jewelryTable;

import io.wispforest.owo.util.ImplementedInventory;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.jewelry.AbstractJewelryRecipe;
import ru.feytox.etherology.recipes.jewelry.BrokenRecipe;
import ru.feytox.etherology.recipes.jewelry.LensRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipeSerializer;
import ru.feytox.etherology.registry.misc.RecipesRegistry;

import java.util.List;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class JewelryTableInventory implements ImplementedInventory {

    public static final List<Integer> EMPTY_CELLS;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    @Nullable @Getter
    private final JewelryBlockEntity parent;
    @Nullable
    private Identifier currentRecipe;

    public void tryCraft(ServerWorld world) {
        AbstractJewelryRecipe recipe = getRecipe(world);
        if (recipe == null) return;

        ItemStack newLens = recipe.craft(this, world.getRegistryManager());

        setStack(0, newLens);
        markDirty();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
        if (stack.isEmpty()) {
            resetRecipe();
            return;
        }

        if (parent == null || !(parent.getWorld() instanceof ServerWorld serverWorld)) return;
        updateRecipe(serverWorld);
    }

    public void updateRecipe(ServerWorld world) {
        RecipeEntry<? extends AbstractJewelryRecipe> recipe = RecipesRegistry.getFirstMatch(world, this, LensRecipeSerializer.INSTANCE);
        if (recipe == null) recipe = RecipesRegistry.getFirstMatch(world, this, ModifierRecipeSerializer.INSTANCE);
        if (recipe == null) recipe = getBrokenRecipe();

        if (recipe == null) currentRecipe = null;
        else currentRecipe = recipe.id();
    }

    @Nullable
    private RecipeEntry<BrokenRecipe> getBrokenRecipe() {
        return LensComponent.get(getStack(0))
                .filter(component -> component.pattern().isCracked())
                .map(component -> BrokenRecipe.INSTANCE).orElse(null);
    }

    public boolean hasRecipe() {
        return currentRecipe != null;
    }

    @Nullable
    public AbstractJewelryRecipe getRecipe(ServerWorld world) {
        if (currentRecipe != null) {
            if (currentRecipe.equals(BrokenRecipe.INSTANCE.id())) return BrokenRecipe.INSTANCE.value();
            return RecipesRegistry.maybeGet(world, currentRecipe).map(entry -> entry.value() instanceof AbstractJewelryRecipe recipe ? recipe : null).orElse(null);
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
        val lensData = LensComponent.getWrapper(lens).orElse(null);
        if (lensData == null) return;

        LensPattern.Mutable pattern = lensData.getComponent().pattern().asMutable();
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

        lensData.set(pattern, LensComponent::withPattern).save();
    }

    private boolean scanCell(int x, int y, int x0, int y0, LensPattern.Mutable pattern) {
        int cell = (y << 3) | x;
        if (EMPTY_CELLS.contains(cell)) return false;

        boolean isSoft = pattern.isSoft(cell);
        boolean isHard = !isSoft && pattern.isHard(cell);
        if (!isSoft && !isHard) return true;

        int damage = 2;
        if (isHard) damage = 10;
        damageLens(damage);

        fill(pattern, x0, y0, x, y);
        return false;
    }

    private void fill(LensPattern.Mutable pattern, int x0, int y0, int x1, int y1) {
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
        val lensData = LensComponent.getWrapper(getStack(0)).orElse(null);
        if (lensData == null) return false;

        LensPattern.Mutable pattern = lensData.getComponent().pattern().asMutable();

        boolean result = isSoft ? pattern.markSoft(index) : pattern.markHard(index);
        if (!result) return false;

        lensData.set(pattern, LensComponent::withPattern).save();
        return true;
    }

    /**
     * @return true if lens broken, otherwise - false
     */
    public boolean damageLens(int amount) {
        return damageLens(getStack(0), amount);
    }

    /**
     * @return true if lens broken, otherwise - false
     */
    public boolean damageLens(ItemStack lensStack, int amount) {
        if (!(getWorld() instanceof ServerWorld world)) return false;
        Item lensItem = lensStack.getItem();
        if (!LensItem.damageLens(world, lensStack, amount)) return false;

        markDirty();
        return onLensDamage(parent, lensStack, lensItem);
    }

    @Nullable
    public World getWorld() {
        return parent == null ? null : parent.getWorld();
    }

    public static boolean onLensDamage(@Nullable JewelryBlockEntity table, ItemStack lensStack, Item lensItem) {
        if (!lensStack.isEmpty()) return false;
        if (table == null) return true;

        World world = table.getWorld();
        if (!(world instanceof ServerWorld serverWorld)) return true;

        Vec3d lensPos = table.getPos().toCenterPos().add(-0.5, 0.5, -0.5);
        LensItem.playLensBrakeSound(serverWorld, lensPos);
        LensItem.spawnLensBrakeParticles(serverWorld, lensItem, lensPos, -90, 0);
        return true;
    }

    public int getTextureOffset(int index) {
        return LensComponent.get(getStack(0)).map(LensComponent::pattern)
                .map(pattern -> pattern.getTextureOffset(index))
                .orElse(0);
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

    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, items, registryLookup);
        String recipeStr = currentRecipe == null ? "" : currentRecipe.toString();
        nbt.putString("recipe", recipeStr);
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        items.clear();
        Inventories.readNbt(nbt, items, registryLookup);
        String recipeStr = nbt.getString("recipe");
        currentRecipe = recipeStr.isEmpty() ? null : Identifier.tryParse(recipeStr);
    }

    static {
        EMPTY_CELLS = IntArrayList.of(0, 1, 6, 7, 8, 15, 48, 55, 56, 57, 62, 63);
    }
}
