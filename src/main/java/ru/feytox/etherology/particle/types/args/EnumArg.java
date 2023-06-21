package ru.feytox.etherology.particle.types.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

public class EnumArg<T extends Enum<T>> extends ParticleArg<T> {
    private final Class<T> enumClass;

    private EnumArg(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public static <E extends Enum<E>> EnumArg<E> of(Class<E> enumClass) {
        return new EnumArg<>(enumClass);
    }

    @Override
    public T read(StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(enumClass, reader.readString());
    }

    @Override
    public T read(PacketByteBuf buf) {
        return buf.readEnumConstant(enumClass);
    }

    @Override
    public PacketByteBuf write(PacketByteBuf buf, @NotNull T value) {
        buf.writeEnumConstant(value);
        return buf;
    }

    @Override
    public String write(@NotNull T value) {
        return value.name();
    }

    @Override
    public MapCodec<T> getCodec(String fieldName) {
        return Codec.INT.xmap(
                (ordinal) -> enumClass.getEnumConstants()[ordinal],
                Enum::ordinal
        ).fieldOf(fieldName);
    }
}
