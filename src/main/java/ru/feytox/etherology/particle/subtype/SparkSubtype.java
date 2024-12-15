package ru.feytox.etherology.particle.subtype;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.seal.SealType;
import ru.feytox.etherology.util.misc.CodecUtil;

public enum SparkSubtype implements StringIdentifiable {
    SIMPLE,
    KETA,
    RELLA,
    VIA,
    CLOS,
    RISING,
    JEWELRY;

    public static final Codec<SparkSubtype> CODEC = StringIdentifiable.createBasicCodec(SparkSubtype::values);
    public static final PacketCodec<ByteBuf, SparkSubtype> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Nullable
    public static SparkSubtype of(SealType sealType) {
        return EnumUtils.getEnum(SparkSubtype.class, sealType.name(), null);
    }

    @Override
    public String asString() {
        return name();
    }
}
