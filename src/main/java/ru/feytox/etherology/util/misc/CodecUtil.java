package ru.feytox.etherology.util.misc;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import lombok.experimental.UtilityClass;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

@UtilityClass
public class CodecUtil {

    public static final Codec<IntArraySet> INT_SET = Codec.list(Codec.INT).xmap(IntArraySet::new, IntArrayList::new).stable();
    public static final PacketCodec<ByteBuf, Vec3d> VEC3D_PACKET = PacketCodec.tuple(PacketCodecs.DOUBLE, Vec3d::getX, PacketCodecs.DOUBLE, Vec3d::getY, PacketCodecs.DOUBLE, Vec3d::getZ, Vec3d::new);
    public static final PacketCodec<ByteBuf, Item> ITEM_PACKET = PacketCodecs.codec(Registries.ITEM.getCodec());
    public static final PacketCodec<ByteBuf, IntArraySet> INT_SET_PACKET = PacketCodecs.VAR_INT.collect(PacketCodecs.toCollection(IntArraySet::new));

    public static <T extends Enum<T>> PacketCodec<ByteBuf, T> ofEnum(T[] values) {
        return PacketCodecs.indexed(ValueLists.createIdToValueFunction((ToIntFunction<T>) Enum::ordinal, values, ValueLists.OutOfBoundsHandling.ZERO), Enum::ordinal);
    }

    /**
     * @see PacketCodec#unit(Object)
     */
    public static <B, V> PacketCodec<B, V> unitUnchecked(final V value) {
        return new PacketCodec<>() {
            public V decode(B object) {
                return value;
            }

            public void encode(B object, V object2) {}
        };
    }

    /**
     * PacketCodec for Map without strong types for Map
     * @see PacketCodecs#map(IntFunction, PacketCodec, PacketCodec)
     */
    public static <B extends ByteBuf, K, V> PacketCodec<B, Map<K, V>> map(IntFunction<Map<K, V>> factory, PacketCodec<? super B, K> keyCodec, PacketCodec<? super B, V> valueCodec) {
        return PacketCodecs.map(factory, keyCodec, valueCodec);
    }
}
