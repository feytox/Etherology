package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import ru.feytox.etherology.particle.effects.args.EnumArg;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

@ApiStatus.Experimental
@Deprecated
public class TypedParticleEffect<T extends Enum<T>> extends FeyParticleEffect<TypedParticleEffect<T>> {

    private final ParticleArg<T> enumArg;
    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();

    public TypedParticleEffect(ParticleType<TypedParticleEffect<T>> type, T particleType, Vec3d moveVec) {
        this(type, particleType.getDeclaringClass());
        enumArg.setValue(particleType);
        moveVecArg.setValue(moveVec);
    }

    public TypedParticleEffect(ParticleType<TypedParticleEffect<T>> type, Class<T> enumClass) {
        super(type);
        enumArg = EnumArg.of(enumClass);
    }

    @Override
    public TypedParticleEffect<T> read(ParticleType<TypedParticleEffect<T>> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        T particleType = enumArg.read(reader);
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new TypedParticleEffect<>(type, particleType, moveVec);
    }

    @Override
    public TypedParticleEffect<T> read(ParticleType<TypedParticleEffect<T>> type, PacketByteBuf buf) {
        T particleType = enumArg.read(buf);
        Vec3d moveVec = moveVecArg.read(buf);
        return new TypedParticleEffect<>(type, particleType, moveVec);
    }

    @Override
    public String write() {
        return enumArg.write() + " " + moveVecArg.write();
    }

    @Override
    public Codec<TypedParticleEffect<T>> createCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                enumArg.getCodec("particle_type").forGetter(TypedParticleEffect::getParticleType),
                moveVecArg.getCodec("move_vec").forGetter(TypedParticleEffect::getMoveVec)
        ).apply(instance, (particleType, moveVec) -> new TypedParticleEffect<>(type, particleType, moveVec)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        enumArg.write(buf);
        moveVecArg.write(buf);
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    public T getParticleType() {
        return enumArg.getValue();
    }
}
