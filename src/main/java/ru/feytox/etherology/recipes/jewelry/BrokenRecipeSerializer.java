package ru.feytox.etherology.recipes.jewelry;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

public class BrokenRecipeSerializer extends FeyRecipeSerializer<BrokenRecipe> {

    public static final BrokenRecipeSerializer INSTANCE = new BrokenRecipeSerializer();
    private static final MapCodec<BrokenRecipe> CODEC = MapCodec.unit(BrokenRecipe.INSTANCE.value());
    private static final PacketCodec<RegistryByteBuf, BrokenRecipe> PACKET_CODEC = PacketCodec.unit(BrokenRecipe.INSTANCE.value());

    public BrokenRecipeSerializer() {
        super("broken_recipe");
    }

    @Override
    public MapCodec<BrokenRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, BrokenRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
