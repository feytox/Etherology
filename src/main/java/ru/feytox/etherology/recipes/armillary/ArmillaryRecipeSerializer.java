package ru.feytox.etherology.recipes.armillary;

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

public class ArmillaryRecipeSerializer extends FeyRecipeSerializer<ArmillaryRecipe> {

    public static final ArmillaryRecipeSerializer INSTANCE = new ArmillaryRecipeSerializer();

    private static final MapCodec<ArmillaryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("center_input").forGetter(ArmillaryRecipe::getCenterInput),
            Aspect.CODEC.listOf().fieldOf("aspects").forGetter(ArmillaryRecipe::getAspects),
            Codec.FLOAT.fieldOf("ether_points").forGetter(ArmillaryRecipe::getEtherPoints),
            ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(ArmillaryRecipe::getOutput)
    ).apply(instance, ArmillaryRecipe::new));

    private static final PacketCodec<RegistryByteBuf, ArmillaryRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, ArmillaryRecipe::getCenterInput,
            Aspect.LIST_PACKET_CODEC, ArmillaryRecipe::getAspects,
            PacketCodecs.FLOAT, ArmillaryRecipe::getEtherPoints,
            ItemStack.PACKET_CODEC, ArmillaryRecipe::getOutput, ArmillaryRecipe::new);

    public ArmillaryRecipeSerializer() {
        super("armillary_recipe");
    }

    @Override
    public MapCodec<ArmillaryRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ArmillaryRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
