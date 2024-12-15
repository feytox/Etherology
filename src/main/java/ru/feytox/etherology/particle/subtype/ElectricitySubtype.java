package ru.feytox.etherology.particle.subtype;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.util.misc.CodecUtil;

public enum ElectricitySubtype implements StringIdentifiable {
    SIMPLE,
    MATRIX,
    JEWELRY;

    public static final Codec<ElectricitySubtype> CODEC = StringIdentifiable.createBasicCodec(ElectricitySubtype::values);
    public static final PacketCodec<ByteBuf, ElectricitySubtype> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Override
    public String asString() {
        return name();
    }
}
