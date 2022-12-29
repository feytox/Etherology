package name.uwu.feytox.etherology.recipes.armillary;

import name.uwu.feytox.etherology.enums.InstabTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EArmillaryRecipe {
    private final ArmillaryRecipe recipe;
    private List<Ingredient> inputs;
    private Ingredient centerInput;
    private List<Integer> deletedInputs = new ArrayList<>();
    private final float etherPoints;
    private final ItemStack outputStack;
    private final InstabTypes instability;
    private int lastDeletedIndex = -1;

    public EArmillaryRecipe(ArmillaryRecipe recipe) {
        this.recipe = recipe;
        this.inputs = new ArrayList<>(recipe.getInputs());
        this.centerInput = recipe.getCenterInput();
        this.etherPoints = recipe.getEtherPoints();
        this.outputStack = recipe.getOutput();
        this.instability = recipe.getInstability();
    }

    public EArmillaryRecipe(ArmillaryRecipe recipe, List<Integer> deletedInputs, int lastDeletedIndex) {
        this(recipe);
        this.deletedInputs = deletedInputs;
        this.lastDeletedIndex = lastDeletedIndex;
        List<Ingredient> clearInputs = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            if (!this.deletedInputs.contains(i)) clearInputs.add(inputs.get(i));
        }
        inputs = clearInputs;
        if (deletedInputs.contains(-1)) centerInput = null;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    public Ingredient getCenterInput() {
        return centerInput;
    }

    public float getEtherPoints() {
        return etherPoints;
    }

    public ItemStack getOutputStack() {
        return outputStack;
    }

    public InstabTypes getInstability() {
        return instability;
    }

    public int findMatchAndRemove(List<ItemStack> items, ItemStack centerItemStack) {
        Ingredient input = getInput();
        if (input != null) {
            for (int i = 0; i < items.size(); i++) {
                if (input.test(items.get(i))) {
                    items.remove(i);
                    popInput();
                    return i;
                }
            }
        }
        else {
            Ingredient centerInput = getCenterInput();
            if (centerInput != null && centerInput.test(centerItemStack)) {
                popCenterInput();
                return -1;
            }
        }
        return -621;
    }

    @Nullable
    public Ingredient getInput() {
        if (!inputs.isEmpty()) return inputs.get(0);
        return null;
    }

    @Nullable
    public Ingredient popInput() {
        if (!inputs.isEmpty()) {
            lastDeletedIndex++;
            deletedInputs.add(lastDeletedIndex);
            return inputs.remove(0);
        }
        return null;
    }

    @Nullable
    public Ingredient popCenterInput() {
        if (centerInput == null) return null;
        Ingredient result = centerInput;
        centerInput = null;
        deletedInputs.add(-1);
        return result;
    }

    public static EArmillaryRecipe readNbt(NbtCompound nbt, World world) {
        if (world == null || world.isClient) return null;

        String recipeIDStr = nbt.getString("recipeID");
        if (recipeIDStr.equals("null:null")) return null;
        Identifier recipeID = new Identifier(recipeIDStr);
        Optional<?> match = world.getRecipeManager().get(recipeID);

        if (match.isEmpty()) return null;
        if (!(match.get() instanceof ArmillaryRecipe recipe)) return null;

        int[] ints = nbt.getIntArray("deletedInputs");
        List<Integer> deletedInputs = Arrays.stream(ints).boxed().toList();

        int lastDeletedIndex = nbt.getInt("lastDeletedIndex");

        return new EArmillaryRecipe(recipe, deletedInputs, lastDeletedIndex);
    }

    private void writeToNbt(NbtCompound nbt) {
        nbt.putString("recipeID", recipe.getId().toString());
        nbt.putIntArray("deletedInputs", deletedInputs);
        nbt.putInt("lastDeletedIndex", lastDeletedIndex);
    }

    public static void writeNbt(EArmillaryRecipe lRecipe, NbtCompound nbt) {
        if (lRecipe != null) lRecipe.writeToNbt(nbt);

        nbt.putString("recipeID", "null:null");
        nbt.putIntArray("deletedInputs", new ArrayList<>());
        nbt.putInt("lastDeletedIndex", -1);
    }

    public boolean isFinished() {
        return inputs.isEmpty() && (centerInput == null || centerInput.isEmpty());
    }
}
