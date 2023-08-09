package ru.feytox.etherology.recipes.armillary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.wispforest.owo.nbt.NbtKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.armillar_new.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.util.nbt.Nbtable;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class MatrixRecipe implements Nbtable {

    private static final NbtKey.ListKey<String> INPUTS_KEY = new NbtKey.ListKey<>("inputs", NbtKey.Type.STRING);
    private static final Gson GSON = new Gson();

    private Identifier recipeId;
    private float etherPoints;
    private List<Ingredient> inputs;
    @Nullable
    private Ingredient centerInput;

    public static MatrixRecipe of(ArmillaryRecipe recipe) {
        return new MatrixRecipe(recipe.getId(), recipe.getEtherPoints(), new ObjectArrayList<>(recipe.getInputs()), recipe.getCenterInput());
    }

    public void setEtherPoints(float value) {
        etherPoints = Math.max(value, 0);
    }

    public Optional<PedestalBlockEntity> findMatchedPedestal(List<PedestalBlockEntity> pedestals) {
        if (inputs.isEmpty()) return Optional.empty();
        Ingredient input = inputs.get(0);

        for (PedestalBlockEntity pedestal : pedestals) {
            ItemStack stack = pedestal.getStack(0);
            if (input.test(stack)) return Optional.of(pedestal);
        }

        return Optional.empty();
    }

    public boolean testCenterStack(ArmillaryMatrixBlockEntity matrix) {
        if (centerInput == null) return true;
        return centerInput.test(matrix.getStack(0));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound root = new NbtCompound();

        root.putString("recipe_id", recipeId.toString());
        root.putFloat("ether_points", etherPoints);
        root.putString("center_input", centerInput == null ? "" : centerInput.toJson().toString());

        val nbtInputs = inputs.stream().map(Ingredient::toJson).map(JsonElement::toString).map(NbtString::of).toList();
        NbtList nbtInputsList = new NbtList();
        nbtInputsList.addAll(nbtInputs);
        root.put(INPUTS_KEY, nbtInputsList);

        nbt.put("matrix_recipe", root);
    }

    @Nullable
    public static MatrixRecipe readFromNbt(NbtCompound nbt) {
        NbtCompound root = nbt.getCompound("matrix_recipe");
        if (root.isEmpty()) return null;

        Identifier recipeId = new Identifier(root.getString("recipe_id"));
        float etherPoints = root.getFloat("ether_points");
        String centerInputJson = root.getString("center_input");
        Ingredient centerInput = centerInputJson.isEmpty() ? null : readIngredient(centerInputJson);

        List<Ingredient> inputs = root.get(INPUTS_KEY).stream().map(NbtElement::asString).map(MatrixRecipe::readIngredient).toList();
        return new MatrixRecipe(recipeId, etherPoints, inputs, centerInput);
    }

    @Override
    @Nullable
    public Nbtable readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }

    private static Ingredient readIngredient(String jsonString) {
        return Ingredient.fromJson(GSON.fromJson(jsonString, JsonElement.class));
    }

    public boolean isFinished() {
        return inputs.isEmpty() && (centerInput == null || centerInput.isEmpty());
    }
}
