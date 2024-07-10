package ru.feytox.etherology.recipes.brewingCauldron;

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

public class CauldronRecipeSerializer extends FeyRecipeSerializer<CauldronRecipe> {

    public static final CauldronRecipeSerializer INSTANCE = new CauldronRecipeSerializer();

    private static final MapCodec<CauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("inputItem").forGetter(CauldronRecipe::getInputItem),
            Codec.INT.fieldOf("inputAmount").orElse(1).forGetter(CauldronRecipe::getInputAmount),
            AspectContainer.CODEC.fieldOf("inputAspects").forGetter(CauldronRecipe::getInputAspects),
            ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(CauldronRecipe::getOutput)
    ).apply(instance, CauldronRecipe::new));

    private static final PacketCodec<RegistryByteBuf, CauldronRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, CauldronRecipe::getInputItem,
            PacketCodecs.VAR_INT, CauldronRecipe::getInputAmount,
            AspectContainer.PACKET_CODEC, CauldronRecipe::getInputAspects,
            ItemStack.PACKET_CODEC, CauldronRecipe::getOutput, CauldronRecipe::new);

    public CauldronRecipeSerializer() {
        super("brewing_cauldron_recipe");
    }

    @Override
    public MapCodec<CauldronRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, CauldronRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
