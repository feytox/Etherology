package ru.feytox.etherology.particle.subtype;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.util.misc.CodecUtil;

public enum LightSubtype implements StringIdentifiable {
    SIMPLE,
    SPARK,
    PUSHING,
    ATTRACT,
    BREWING,
    MATRIX,
    GENERATOR,
    HAZE;

    public static final Codec<LightSubtype> CODEC = StringIdentifiable.createBasicCodec(LightSubtype::values);
    public static final PacketCodec<ByteBuf, LightSubtype> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Override
    public String asString() {
        return name();
    }
}
