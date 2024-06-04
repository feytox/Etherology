package ru.feytox.etherology.recipes.jewelry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.stream.Collectors;

public class ModifierRecipeSerializer extends FeyRecipeSerializer<ModifierRecipe> {

    public static final ModifierRecipeSerializer INSTANCE = new ModifierRecipeSerializer();

    public ModifierRecipeSerializer() {
        super("modifier_recipe");
    }

    @Override
    public ModifierRecipe read(Identifier id, JsonObject json) {
        JsonArray patternJson = JsonHelper.getArray(json, "pattern");
        char[] flatPattern = patternJson.asList().stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.joining()).toCharArray();

        LensPattern pattern = LensRecipeSerializer.readPattern(flatPattern);

        String outputId = JsonHelper.getString(json, "outputModifier");
        LensModifier modifier = LensModifier.get(new Identifier(outputId));
        if (modifier == null) throw new JsonSyntaxException("No such lens modifier " + outputId);

        int ether = JsonHelper.getInt(json, "ether");

        return new ModifierRecipe(pattern, modifier, ether, id);
    }

    @Override
    public ModifierRecipe read(Identifier id, PacketByteBuf buf) {
        LensPattern pattern = LensPattern.readBuf(buf);
        String outputId = buf.readString();
        int ether = buf.readInt();
        LensModifier modifier = LensModifier.get(new Identifier(outputId));
        if (modifier == null) throw new JsonSyntaxException("No such lens modifier " + outputId);
        return new ModifierRecipe(pattern, modifier, ether, id);
    }

    @Override
    public void write(PacketByteBuf buf, ModifierRecipe recipe) {
        recipe.getPattern().writeBuf(buf);
        buf.writeString(recipe.getModifier().modifierId().toString());
        buf.writeInt(recipe.getEther());
    }
}
