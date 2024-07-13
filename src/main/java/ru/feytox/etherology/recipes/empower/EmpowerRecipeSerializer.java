package ru.feytox.etherology.recipes.empower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

public class EmpowerRecipeSerializer extends FeyRecipeSerializer<EmpowerRecipe> {

    public static final EmpowerRecipeSerializer INSTANCE = new EmpowerRecipeSerializer();

    private static final MapCodec<EmpowerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EmpowerRecipe.Pattern.CODEC.forGetter(EmpowerRecipe::getPattern),
            Codec.INT.fieldOf("rellaCount").orElse(0).forGetter(EmpowerRecipe::getRellaCount),
            Codec.INT.fieldOf("viaCount").orElse(0).forGetter(EmpowerRecipe::getViaCount),
            Codec.INT.fieldOf("closCount").orElse(0).forGetter(EmpowerRecipe::getClosCount),
            Codec.INT.fieldOf("ketaCount").orElse(0).forGetter(EmpowerRecipe::getKetaCount),
            ItemStack.CODEC.fieldOf("result").forGetter(EmpowerRecipe::getOutput)
    ).apply(instance, EmpowerRecipe::new));

    private static final PacketCodec<RegistryByteBuf, EmpowerRecipe> PACKET_CODEC = PacketCodec.tuple(
            EmpowerRecipe.Pattern.PACKET_CODEC, EmpowerRecipe::getPattern,
            PacketCodecs.VAR_INT, EmpowerRecipe::getRellaCount,
            PacketCodecs.VAR_INT, EmpowerRecipe::getViaCount,
            PacketCodecs.VAR_INT, EmpowerRecipe::getClosCount,
            PacketCodecs.VAR_INT, EmpowerRecipe::getKetaCount,
            ItemStack.PACKET_CODEC, EmpowerRecipe::getOutput, EmpowerRecipe::new);

    public EmpowerRecipeSerializer() {
        super("empower_recipe");
    }

    @Override
    public MapCodec<EmpowerRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, EmpowerRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
