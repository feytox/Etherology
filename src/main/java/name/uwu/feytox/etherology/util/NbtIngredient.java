package name.uwu.feytox.etherology.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

public class NbtIngredient implements Nbtable {
    private final Ingredient ingredient;
    private String name = "ingredient";

    public NbtIngredient() {
        this.ingredient = null;
    }

    private NbtIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    private NbtIngredient(String name, Ingredient ingredient) {
        this(ingredient);
        this.name = name;
    }

    public static NbtIngredient of(String name, Ingredient ingredient) {
        return new NbtIngredient(name, ingredient);
    }

    public static NbtIngredient of(Ingredient ingredient) {
        return new NbtIngredient(ingredient);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound subNbt = new NbtCompound();
        subNbt.putString("ingredient_json", JsonHelper.toSortedString(ingredient.toJson()));

        nbt.put(name, subNbt);
    }

    public static NbtIngredient readFromNbt(String name, NbtCompound nbt) {
        NbtCompound subNbt = nbt.getCompound(name);
        String jsonString = subNbt.getString("ingredient_json");

        return new NbtIngredient(Ingredient.fromJson(JsonHelper.deserialize(jsonString)));
    }

    public static NbtIngredient readFromNbt(NbtCompound nbt) {
        return readFromNbt("ingredient", nbt);
    }

    @Override
    public NbtIngredient readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }
}
