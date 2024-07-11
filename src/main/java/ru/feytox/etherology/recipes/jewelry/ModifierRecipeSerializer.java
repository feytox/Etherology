package ru.feytox.etherology.recipes.jewelry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

public class ModifierRecipeSerializer extends FeyRecipeSerializer<ModifierRecipe> {

    public static final ModifierRecipeSerializer INSTANCE = new ModifierRecipeSerializer();

    private static final MapCodec<ModifierRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbstractJewelryRecipe.Pattern.CODEC.fieldOf("pattern").forGetter(AbstractJewelryRecipe::getPattern),
            LensModifier.CODEC.fieldOf("outputModifier").forGetter(ModifierRecipe::getModifier),
            Codec.INT.fieldOf("ether").forGetter(AbstractJewelryRecipe::getEther)
    ).apply(instance, ModifierRecipe::new));

    private static final PacketCodec<RegistryByteBuf, ModifierRecipe> PACKET_CODEC = PacketCodec.tuple(
            AbstractJewelryRecipe.Pattern.PACKET_CODEC, AbstractJewelryRecipe::getPattern,
            LensModifier.PACKET_CODEC, ModifierRecipe::getModifier,
            PacketCodecs.VAR_INT, AbstractJewelryRecipe::getEther, ModifierRecipe::new);

    public ModifierRecipeSerializer() {
        super("modifier_recipe");
    }

    @Override
    public MapCodec<ModifierRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ModifierRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
