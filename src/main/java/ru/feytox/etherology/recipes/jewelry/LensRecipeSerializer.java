package ru.feytox.etherology.recipes.jewelry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.util.misc.CodecUtil;

public class LensRecipeSerializer extends FeyRecipeSerializer<LensRecipe> {

    public static final LensRecipeSerializer INSTANCE = new LensRecipeSerializer();

    private static final MapCodec<LensRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LensRecipe.Pattern.CODEC.fieldOf("pattern").forGetter(AbstractJewelryRecipe::getPattern),
            Registries.ITEM.getCodec().fieldOf("result").forGetter(LensRecipe::getOutputItem),
            Codec.INT.fieldOf("ether").forGetter(AbstractJewelryRecipe::getEther)
    ).apply(instance, LensRecipe::new));

    private static final PacketCodec<RegistryByteBuf, LensRecipe> PACKET_CODEC = PacketCodec.tuple(
            AbstractJewelryRecipe.Pattern.PACKET_CODEC, AbstractJewelryRecipe::getPattern,
            CodecUtil.ITEM_PACKET, LensRecipe::getOutputItem,
            PacketCodecs.VAR_INT, AbstractJewelryRecipe::getEther, LensRecipe::new);

    public LensRecipeSerializer() {
        super("lens_recipe");
    }

    @Override
    public MapCodec<LensRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, LensRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
