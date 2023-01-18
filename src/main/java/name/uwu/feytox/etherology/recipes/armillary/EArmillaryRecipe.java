package name.uwu.feytox.etherology.recipes.armillary;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import name.uwu.feytox.etherology.enums.InstabTypes;
import name.uwu.feytox.etherology.util.FeyNbtList;
import name.uwu.feytox.etherology.util.NbtIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EArmillaryRecipe {
    private ArmillaryRecipe recipe;
    private List<Ingredient> inputs;
    private Ingredient centerInput;
    private List<Integer> deletedInputs = new ArrayList<>();
    private final float etherPoints;
    private final ItemStack outputStack;
    private final InstabTypes instability;
    private int lastDeletedIndex = -1;

    public EArmillaryRecipe(ArmillaryRecipe recipe) {
        this.inputs = new ArrayList<>(recipe.getInputs());
        this.centerInput = recipe.getCenterInput();
        this.etherPoints = recipe.getEtherPoints();
        this.outputStack = recipe.getOutput();
        this.instability = recipe.getInstability();
    }

    public EArmillaryRecipe(List<Ingredient> inputs, Ingredient centerInput, float etherPoints, ItemStack outputStack,
                            InstabTypes instability, List<Integer> deletedInputs, int lastDeletedIndex) {
        this.inputs = inputs;
        this.centerInput = centerInput;
        this.etherPoints = etherPoints;
        this.outputStack = outputStack;
        this.instability = instability;
        this.deletedInputs = deletedInputs;
        this.lastDeletedIndex = lastDeletedIndex;
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

//    public static EArmillaryRecipe readNbt(NbtCompound nbt) {
//        String recipeIDStr = nbt.getString("recipeID");
//        if (recipeIDStr.equals("null:null")) return null;
//        Identifier recipeID = new Identifier(recipeIDStr);
//        Optional<?> match =
//
//        if (match.isEmpty()) return null;
//        if (!(match.get() instanceof ArmillaryRecipe recipe)) return null;
//
//        int[] ints = nbt.getIntArray("deletedInputs");
//        List<Integer> deletedInputs = Arrays.stream(ints).boxed().toList();
//
//        int lastDeletedIndex = nbt.getInt("lastDeletedIndex");
//
//        return new EArmillaryRecipe(recipe, deletedInputs, lastDeletedIndex);
//    }

    public static EArmillaryRecipe readNbt(NbtCompound nbt) {
        NbtCompound subNbt = nbt.getCompound("armillary_recipe");
        if (subNbt.isEmpty()) return null;

        FeyNbtList<NbtIngredient> nbtInputs = FeyNbtList.readFromNbt("inputs", NbtIngredient.class, subNbt);
        if (nbtInputs == null) return null;
        List<Ingredient> inputs = Lists.transform(nbtInputs.getList(), NbtIngredient::getIngredient);

        Ingredient centerInput = NbtIngredient.readFromNbt("center_input", subNbt).getIngredient();
        float ether_points = subNbt.getFloat("ether_points");
        ItemStack outputStack = ItemStack.fromNbt(subNbt);
        InstabTypes instability = InstabTypes.readNbt(subNbt);

        int[] ints = nbt.getIntArray("deletedInputs");
        List<Integer> deletedInputs = new ArrayList<>(Ints.asList(ints));;
        int lastDeletedIndex = nbt.getInt("lastDeletedIndex");

        return new EArmillaryRecipe(inputs, centerInput, ether_points, outputStack, instability, deletedInputs, lastDeletedIndex);
    }

    private void writeToNbt(NbtCompound nbt) {
        FeyNbtList<NbtIngredient> inputs = new FeyNbtList<>("inputs", this.inputs.stream().map(NbtIngredient::of).collect(Collectors.toList()));
        inputs.writeNbt(nbt);

        NbtIngredient centerInput = NbtIngredient.of("center_input", this.centerInput);
        centerInput.writeNbt(nbt);

        nbt.putFloat("ether_points", etherPoints);
        outputStack.writeNbt(nbt);
        instability.writeNbt(nbt);

        nbt.putIntArray("deletedInputs", deletedInputs);
        nbt.putInt("lastDeletedIndex", lastDeletedIndex);
    }

    public static void writeNbt(EArmillaryRecipe lRecipe, NbtCompound nbt) {
        NbtCompound subNbt = new NbtCompound();

        if (lRecipe != null) {
            lRecipe.writeToNbt(subNbt);
            nbt.put("armillary_recipe", subNbt);
            return;
        }

        nbt.put("armillary_recipe", subNbt);
    }

    public boolean isFinished() {
        return inputs.isEmpty() && (centerInput == null || centerInput.isEmpty());
    }
}
