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

import java.util.function.ToIntFunction;

@UtilityClass
public class CodecUtil {

    public static final Codec<IntArraySet> INT_SET = Codec.list(Codec.INT).xmap(IntArraySet::new, IntArrayList::new).stable();
    public static final PacketCodec<ByteBuf, Vec3d> VEC3D_PACKET = PacketCodec.tuple(PacketCodecs.DOUBLE, Vec3d::getX, PacketCodecs.DOUBLE, Vec3d::getY, PacketCodecs.DOUBLE, Vec3d::getZ, Vec3d::new);
    public static final PacketCodec<ByteBuf, Item> ITEM_PACKET = PacketCodecs.codec(Registries.ITEM.getCodec());

    public static <T extends Enum<T>> PacketCodec<ByteBuf, T> ofEnum(T[] values) {
        return PacketCodecs.indexed(ValueLists.createIdToValueFunction((ToIntFunction<T>) Enum::ordinal, values, ValueLists.OutOfBoundsHandling.ZERO), Enum::ordinal);
    }
}
