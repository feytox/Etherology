package ru.feytox.etherology.particle.types.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

@Slf4j
public abstract class ParticleArg<T> {
    @Nullable
    @Getter
    @Setter
    private T value;

    public abstract T read(StringReader reader) throws CommandSyntaxException;

    public abstract T read(PacketByteBuf buf);

    public abstract PacketByteBuf write(PacketByteBuf buf, @NonNull T value);

    public abstract String write(@NonNull T value);

    public abstract MapCodec<T> getCodec(String fieldName);

    public PacketByteBuf write(PacketByteBuf buf) {
        if (value == null) {
            new NullPointerException("ParticleArg was not filled with value").printStackTrace();
        }
        return value != null ? write(buf, value) : buf;
    }

    public String write() {
        if (value == null) {
            new NullPointerException("ParticleArg was not filled with value").printStackTrace();
        }
        return value != null ? write(value) : "";
    }
}
