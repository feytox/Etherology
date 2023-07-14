package ru.feytox.etherology.recipes.brewingCauldron;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.registry.util.ResourceReloaders;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class CauldronRecipeSerializer implements RecipeSerializer<CauldronRecipe> {
    public static final CauldronRecipeSerializer INSTANCE = new CauldronRecipeSerializer();
    public static final Identifier ID = new EIdentifier("brewing_cauldron_recipe");

    private CauldronRecipeSerializer() {}

    @Override
    public CauldronRecipe read(Identifier id, JsonObject json) {
        CauldronRecipeJsonFormat recipeJson = ResourceReloaders.EGSON.fromJson(json, CauldronRecipeJsonFormat.class);
        if (recipeJson.inputItem == null || recipeJson.inputAspects == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;
        if (recipeJson.inputAmount == 0) recipeJson.inputAmount = 1;

        Ingredient inputStack = Ingredient.fromJson(recipeJson.inputItem);
        EtherAspectsContainer inputAspects = recipeJson.inputAspects;
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack outputStack = new ItemStack(outputItem, recipeJson.outputAmount);

        return new CauldronRecipe(inputStack, recipeJson.inputAmount, inputAspects, outputStack, id);
    }

    @Override
    public CauldronRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient inputItem = Ingredient.fromPacket(buf);
        int inputAmount = buf.readInt();
        EtherAspectsContainer inputAspects = EtherAspectsContainer.readBuf(buf);
        ItemStack outputStack = buf.readItemStack();

        return new CauldronRecipe(inputItem, inputAmount, inputAspects, outputStack, id);
    }

    @Override
    public void write(PacketByteBuf buf, CauldronRecipe recipe) {
        recipe.getInputItem().write(buf);
        buf.writeInt(recipe.getInputAmount());
        recipe.getInputAspects().writeBuf(buf);
        buf.writeItemStack(recipe.getOutput());
    }
}