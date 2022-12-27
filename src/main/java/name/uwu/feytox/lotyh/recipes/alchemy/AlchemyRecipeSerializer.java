package name.uwu.feytox.lotyh.recipes.alchemy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class AlchemyRecipeSerializer implements RecipeSerializer<AlchemyRecipe> {
    private AlchemyRecipeSerializer() {
    }

    public static final AlchemyRecipeSerializer INSTANCE = new AlchemyRecipeSerializer();
    public static final Identifier ID = new Identifier("lotyh:alchemy_recipe");

    @Override
    public AlchemyRecipe read(Identifier id, JsonObject json) {
        AlchemyRecipeJsonFormat recipeJson = new Gson().fromJson(json, AlchemyRecipeJsonFormat.class);
        if (recipeJson.inputs == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException(id.getPath() + " <-- A required attribute is missing!");
        }
        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;

        List<Ingredient> inputs = new ArrayList<>();
        recipeJson.inputs.forEach(jsonElement -> {
            inputs.add(Ingredient.fromJson(jsonElement.getAsJsonObject()));
        });
        Item outputItem = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, recipeJson.outputAmount);

        return new AlchemyRecipe(inputs, output, id);
    }

    @Override
    public AlchemyRecipe read(Identifier id, PacketByteBuf buf) {
        List<Ingredient> inputs = buf.readList(Ingredient::fromPacket);
        ItemStack output = buf.readItemStack();
        return new AlchemyRecipe(inputs, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, AlchemyRecipe recipe) {
        buf.writeCollection(recipe.getInputs(), (packetByteBuf, ingredient) -> ingredient.write(packetByteBuf));
        buf.writeItemStack(recipe.getOutput());
    }
}
