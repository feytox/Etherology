package ru.feytox.etherology.magic.lens;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.util.misc.CodecUtil;

public enum LensMode implements StringIdentifiable {
    STREAM,
    CHARGE;

    public static final Codec<LensMode> CODEC = StringIdentifiable.createBasicCodec(LensMode::values);
    public static final PacketCodec<ByteBuf, LensMode> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Override
    public String asString() {
        return name();
    }
}
