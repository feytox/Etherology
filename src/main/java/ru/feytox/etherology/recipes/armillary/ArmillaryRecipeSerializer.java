package ru.feytox.etherology.recipes.armillary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.EnumUtils;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.List;
import java.util.stream.Collectors;

public class ArmillaryRecipeSerializer extends FeyRecipeSerializer<ArmillaryRecipe> {

    public static final ArmillaryRecipeSerializer INSTANCE = new ArmillaryRecipeSerializer();

    public ArmillaryRecipeSerializer() {
        super("armillary_recipe");
    }

    @Override
    public ArmillaryRecipe read(Identifier id, JsonObject json) {
        ArmillaryRecipeJsonFormat recipeJson = new Gson().fromJson(json, ArmillaryRecipeJsonFormat.class);

        if (recipeJson.aspects == null || recipeJson.center_input == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;

        List<Aspect> aspects = recipeJson.aspects.asList().stream()
                .map(JsonElement::getAsString)
                .map(name -> EnumUtils.getEnumIgnoreCase(Aspect.class, name))
                .collect(Collectors.toCollection(ObjectArrayList::new));

        Ingredient centerInput = Ingredient.fromJson(recipeJson.center_input);
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, recipeJson.outputAmount);

        return new ArmillaryRecipe(centerInput, aspects, recipeJson.ether_points, output, id);
    }

    @Override
    public ArmillaryRecipe read(Identifier id, PacketByteBuf buf) {
        List<Aspect> aspects = buf.readCollection(ObjectArrayList::new, packetBuf -> packetBuf.readEnumConstant(Aspect.class));
        Ingredient centerInput = Ingredient.fromPacket(buf);
        float etherPoints = buf.readFloat();
        ItemStack outputStack = buf.readItemStack();
        return new ArmillaryRecipe(centerInput, aspects, etherPoints, outputStack, id);
    }

    @Override
    public void write(PacketByteBuf buf, ArmillaryRecipe recipe) {
        buf.writeCollection(recipe.getAspects(), PacketByteBuf::writeEnumConstant);
        recipe.getCenterInput().write(buf);
        buf.writeFloat(recipe.getEtherPoints());
        buf.writeItemStack(recipe.getOutput());
    }
}
