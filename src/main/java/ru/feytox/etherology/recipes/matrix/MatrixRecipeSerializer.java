package ru.feytox.etherology.recipes.matrix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

public class MatrixRecipeSerializer extends FeyRecipeSerializer<MatrixRecipe> {

    public static final MatrixRecipeSerializer INSTANCE = new MatrixRecipeSerializer();

    private static final MapCodec<MatrixRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("center_input").forGetter(MatrixRecipe::getCenterInput),
            Aspect.CODEC.listOf().fieldOf("aspects").forGetter(MatrixRecipe::getAspects),
            Codec.FLOAT.fieldOf("ether_points").forGetter(MatrixRecipe::getEtherPoints),
            ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(MatrixRecipe::getOutput)
    ).apply(instance, MatrixRecipe::new));

    private static final PacketCodec<RegistryByteBuf, MatrixRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, MatrixRecipe::getCenterInput,
            Aspect.LIST_PACKET_CODEC, MatrixRecipe::getAspects,
            PacketCodecs.FLOAT, MatrixRecipe::getEtherPoints,
            ItemStack.PACKET_CODEC, MatrixRecipe::getOutput, MatrixRecipe::new);

    public MatrixRecipeSerializer() {
        super("matrix_recipe");
    }

    @Override
    public MapCodec<MatrixRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MatrixRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
