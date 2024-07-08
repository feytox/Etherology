package ru.feytox.etherology.util.misc;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import lombok.experimental.UtilityClass;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

import java.util.function.ToIntFunction;

@UtilityClass
public class CodecUtil {

    public static final Codec<IntArraySet> INT_SET_CODEC = Codec.list(Codec.INT).xmap(IntArraySet::new, IntArrayList::new).stable();

    public static <T extends Enum<T>> PacketCodec<ByteBuf, T> ofEnum(T[] values) {
        return PacketCodecs.indexed(ValueLists.createIdToValueFunction((ToIntFunction<T>) Enum::ordinal, values, ValueLists.OutOfBoundsHandling.ZERO), Enum::ordinal);
    }
}
