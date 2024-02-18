package ru.feytox.etherology.recipes.jewelry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import ru.feytox.etherology.magic.lense.LensPattern;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JewelryRecipeSerializer extends FeyRecipeSerializer<JewelryRecipe> {

    public static final JewelryRecipeSerializer INSTANCE = new JewelryRecipeSerializer();

    public JewelryRecipeSerializer() {
        super("jewelry_recipe");
    }

    @Override
    public JewelryRecipe read(Identifier id, JsonObject json) {
        JsonArray patternJson = JsonHelper.getArray(json, "pattern");
        char[] flatPattern = patternJson.asList().stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.joining()).toCharArray();

        IntArraySet cracks = IntStream.range(1, flatPattern.length)
                .filter(i -> flatPattern[i] == 'X')
                .boxed().collect(Collectors.toCollection(IntArraySet::new));
        IntArraySet softCells = IntStream.range(1, flatPattern.length)
                .filter(i -> flatPattern[i] == 'Y')
                .boxed().collect(Collectors.toCollection(IntArraySet::new));
        LensPattern pattern = new LensPattern(cracks, softCells);

        String outputId = JsonHelper.getString(json, "outputItem");
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(outputId))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + outputId));

        int ether = JsonHelper.getInt(json, "ether");

        return new JewelryRecipe(pattern, outputItem, ether, id);
    }

    @Override
    public JewelryRecipe read(Identifier id, PacketByteBuf buf) {
        LensPattern pattern = LensPattern.readBuf(buf);
        String outputId = buf.readString();
        int ether = buf.readInt();
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(outputId))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + outputId));
        return new JewelryRecipe(pattern, outputItem, ether, id);
    }

    @Override
    public void write(PacketByteBuf buf, JewelryRecipe recipe) {
        recipe.getPattern().writeBuf(buf);
        buf.writeString(Registries.ITEM.getId(recipe.getOutputItem()).toString());
        buf.writeInt(recipe.getEther());
    }
}
