package ru.feytox.etherology.recipes.jewelry;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

public class BrokenRecipeSerializer extends FeyRecipeSerializer<BrokenRecipe> {

    public static final BrokenRecipeSerializer INSTANCE = new BrokenRecipeSerializer();

    public BrokenRecipeSerializer() {
        super("broken_recipe");
    }

    @Override
    public BrokenRecipe read(Identifier id, JsonObject json) {
        return BrokenRecipe.INSTANCE;
    }

    @Override
    public BrokenRecipe read(Identifier id, PacketByteBuf buf) {
        return BrokenRecipe.INSTANCE;
    }

    @Override
    public void write(PacketByteBuf buf, BrokenRecipe recipe) {}
}
