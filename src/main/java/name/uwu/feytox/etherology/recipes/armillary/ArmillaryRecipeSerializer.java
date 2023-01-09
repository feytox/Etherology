package name.uwu.feytox.etherology.recipes.armillary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import name.uwu.feytox.etherology.util.EIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ArmillaryRecipeSerializer implements RecipeSerializer<ArmillaryRecipe> {
    private ArmillaryRecipeSerializer() {}

    public static final ArmillaryRecipeSerializer INSTANCE = new ArmillaryRecipeSerializer();
    public static final Identifier ID = new EIdentifier("armillary_recipe");

    @Override
    public ArmillaryRecipe read(Identifier id, JsonObject json) {
        ArmillaryRecipeJsonFormat recipeJson = new Gson().fromJson(json, ArmillaryRecipeJsonFormat.class);

        if (recipeJson.inputs == null || recipeJson.center_input == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;

        List<Ingredient> inputs = new ArrayList<>();
        recipeJson.inputs.forEach(jsonElement ->
                inputs.add(Ingredient.fromJson(jsonElement.getAsJsonObject())));

        Ingredient centerInput = Ingredient.fromJson(recipeJson.center_input);
        Item outputItem = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, recipeJson.outputAmount);

        return new ArmillaryRecipe(inputs, centerInput, recipeJson.instability, recipeJson.ether_points, output, id);
    }

    @Override
    public ArmillaryRecipe read(Identifier id, PacketByteBuf buf) {
        List<Ingredient> inputs = buf.readList(Ingredient::fromPacket);
        Ingredient centerInput = Ingredient.fromPacket(buf);
        int instability = buf.readInt();
        float etherPoints = buf.readFloat();
        ItemStack output = buf.readItemStack();
        return new ArmillaryRecipe(inputs, centerInput, instability, etherPoints, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, ArmillaryRecipe recipe) {
        buf.writeCollection(recipe.getInputs(), (packetByteBuf, ingredient) -> ingredient.write(packetByteBuf));
        recipe.getCenterInput().write(buf);
        buf.writeInt(recipe.getInstability().getIndex());
        buf.writeFloat(recipe.getEtherPoints());
        buf.writeItemStack(recipe.getOutput());
    }
}
