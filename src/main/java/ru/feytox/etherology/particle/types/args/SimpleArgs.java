package ru.feytox.etherology.particle.types.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@UtilityClass
public class SimpleArgs {
    public static final Supplier<ParticleArg<Vec3d>> VEC3D;
    public static final Supplier<ParticleArg<Double>> DOUBLE;

    static {
        DOUBLE = () -> new ParticleArg<>() {

            @Override
            public Double read(StringReader reader) throws CommandSyntaxException {
                return reader.readDouble();
            }

            @Override
            public Double read(PacketByteBuf buf) {
                return buf.readDouble();
            }

            @Override
            public PacketByteBuf write(PacketByteBuf buf, @NonNull Double value) {
                buf.writeDouble(value);
                return buf;
            }

            @Override
            public String write(@NonNull Double value) {
                return value.toString();
            }

            @Override
            public MapCodec<Double> getCodec(String fieldName) {
                return Codec.DOUBLE.fieldOf(fieldName);
            }
        };

        VEC3D = () -> new ParticleArg<>() {
            @Override
            public Vec3d read(StringReader reader) throws CommandSyntaxException {
                double x = CoordinateArgument.parse(reader).toAbsoluteCoordinate(0);
                reader.expect(' ');
                double y = CoordinateArgument.parse(reader).toAbsoluteCoordinate(0);
                reader.expect(' ');
                double z = CoordinateArgument.parse(reader).toAbsoluteCoordinate(0);
                return new Vec3d(x, y, z);
            }

            @Override
            public Vec3d read(PacketByteBuf buf) {
                return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            }

            @Override
            public PacketByteBuf write(PacketByteBuf buf, @NotNull Vec3d value) {
                buf.writeDouble(value.x);
                buf.writeDouble(value.y);
                buf.writeDouble(value.z);
                return buf;
            }

            @Override
            public String write(@NotNull Vec3d value) {
                return value.x + " " + value.y + " " + value.z;
            }

            @Override
            public MapCodec<Vec3d> getCodec(String fieldName) {
                return Vec3d.CODEC.fieldOf(fieldName);
            }
        };
    }
}
