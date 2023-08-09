package ru.feytox.etherology.deprecated.armillary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.ArrayList;
import java.util.List;

public class OldArmillaryRecipeSerializer implements RecipeSerializer<OldArmillaryRecipe> {
    private OldArmillaryRecipeSerializer() {}

    public static final OldArmillaryRecipeSerializer INSTANCE = new OldArmillaryRecipeSerializer();
    public static final Identifier ID = new EIdentifier("armillary_recipe_old");

    @Override
    public OldArmillaryRecipe read(Identifier id, JsonObject json) {
        OldArmillaryRecipeJsonFormat recipeJson = new Gson().fromJson(json, OldArmillaryRecipeJsonFormat.class);

        if (recipeJson.inputs == null || recipeJson.center_input == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;

        List<Ingredient> inputs = new ArrayList<>();
        recipeJson.inputs.forEach(jsonElement ->
                inputs.add(Ingredient.fromJson(jsonElement.getAsJsonObject())));

        Ingredient centerInput = Ingredient.fromJson(recipeJson.center_input);
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, recipeJson.outputAmount);

        return new OldArmillaryRecipe(inputs, centerInput, recipeJson.instability, recipeJson.ether_points, output, id);
    }

    @Override
    public OldArmillaryRecipe read(Identifier id, PacketByteBuf buf) {
        List<Ingredient> inputs = buf.readList(Ingredient::fromPacket);
        Ingredient centerInput = Ingredient.fromPacket(buf);
        int instability = buf.readInt();
        float etherPoints = buf.readFloat();
        ItemStack output = buf.readItemStack();
        return new OldArmillaryRecipe(inputs, centerInput, instability, etherPoints, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, OldArmillaryRecipe recipe) {
        buf.writeCollection(recipe.getInputs(), (packetByteBuf, ingredient) -> ingredient.write(packetByteBuf));
        recipe.getCenterInput().write(buf);
        buf.writeInt(recipe.getInstability().getIndex());
        buf.writeFloat(recipe.getEtherPoints());
        buf.writeItemStack(recipe.getOutput());
    }
}
