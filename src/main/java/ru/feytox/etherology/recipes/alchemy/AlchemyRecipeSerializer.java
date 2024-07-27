package ru.feytox.etherology.recipes.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

public class AlchemyRecipeSerializer extends FeyRecipeSerializer<AlchemyRecipe> {

    public static final AlchemyRecipeSerializer INSTANCE = new AlchemyRecipeSerializer();

    private static final MapCodec<AlchemyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("inputItem").forGetter(AlchemyRecipe::getInputItem),
            Codec.INT.fieldOf("inputAmount").orElse(1).forGetter(AlchemyRecipe::getInputAmount),
            AspectContainer.CODEC.fieldOf("inputAspects").forGetter(AlchemyRecipe::getInputAspects),
            ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(AlchemyRecipe::getOutput)
    ).apply(instance, AlchemyRecipe::new));

    private static final PacketCodec<RegistryByteBuf, AlchemyRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, AlchemyRecipe::getInputItem,
            PacketCodecs.VAR_INT, AlchemyRecipe::getInputAmount,
            AspectContainer.PACKET_CODEC, AlchemyRecipe::getInputAspects,
            ItemStack.PACKET_CODEC, AlchemyRecipe::getOutput, AlchemyRecipe::new);

    public AlchemyRecipeSerializer() {
        super("alchemy_recipe");
    }

    @Override
    public MapCodec<AlchemyRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, AlchemyRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
