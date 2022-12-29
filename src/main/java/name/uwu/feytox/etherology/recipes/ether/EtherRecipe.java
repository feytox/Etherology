package name.uwu.feytox.etherology.recipes.ether;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class EtherRecipe implements Recipe<ImplementedInventory> {
    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> gridInput;
    private final int heavenlyCount;
    private final int aquaticCount;
    private final int deepCount;
    private final int terrestrialCount;
    private final ItemStack output;
    private final Identifier id;

    public EtherRecipe(DefaultedList<Ingredient> gridInput, int heavenlyCount, int aquaticCount, int deepCount,
                       int terrestrialCount, ItemStack output, Identifier id, int width, int height) {
        this.gridInput = gridInput;
        this.heavenlyCount = heavenlyCount;
        this.aquaticCount = aquaticCount;
        this.deepCount = deepCount;
        this.terrestrialCount = terrestrialCount;
        this.output = output;
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public DefaultedList<Ingredient> getGridInput() {
        return gridInput;
    }

    public int getHeavenlyCount() {
        return heavenlyCount;
    }

    public int getAquaticCount() {
        return aquaticCount;
    }

    public int getDeepCount() {
        return deepCount;
    }

    public int getTerrestrialCount() {
        return terrestrialCount;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean matches(ImplementedInventory inventory, World world) {
        if (inventory.size() != 14) return false;
        for(int i = 0; i <= 3 - this.width; ++i) {
            for(int j = 0; j <= 3 - this.height; ++j) {
                if (this.matchesPattern(inventory, i, j, true)) {
                    return inventory.getStack(9).getCount() >= heavenlyCount
                            && inventory.getStack(10).getCount() >= aquaticCount
                            && inventory.getStack(11).getCount() >= deepCount
                            && inventory.getStack(12).getCount() >= terrestrialCount;
                }

                if (this.matchesPattern(inventory, i, j, false)) {
                    return inventory.getStack(9).getCount() >= heavenlyCount
                            && inventory.getStack(10).getCount() >= aquaticCount
                            && inventory.getStack(11).getCount() >= deepCount
                            && inventory.getStack(12).getCount() >= terrestrialCount;
                }
            }
        }

        return false;

    }

    private boolean matchesPattern(ImplementedInventory inv, int offsetX, int offsetY, boolean flipped) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                int k = i - offsetX;
                int l = j - offsetY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (flipped) {
                        ingredient = this.gridInput.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.gridInput.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(inv.getStack(i + j * 3))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(ImplementedInventory inventory) {
        for (int i = 0; i < 9; i++) {
            ItemStack oldStack = inventory.getStack(i);
            oldStack.decrement(1);
        }
        inventory.getStack(9).decrement(this.getHeavenlyCount());
        inventory.getStack(10).decrement(this.getAquaticCount());
        inventory.getStack(11).decrement(this.getDeepCount());
        inventory.getStack(12).decrement(this.getTerrestrialCount());
        inventory.markDirty();
        return getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EtherRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<EtherRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();

        public static final String ID = "ether_recipe";
    }
}
