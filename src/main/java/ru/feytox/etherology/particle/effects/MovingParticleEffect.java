package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public class MovingParticleEffect extends FeyParticleEffect<MovingParticleEffect> {
    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();

    public MovingParticleEffect(ParticleType<MovingParticleEffect> type, Vec3d moveVec) {
        super(type);
        moveVecArg.setValue(moveVec);
    }

    public MovingParticleEffect(ParticleType<MovingParticleEffect> type) {
        super(type);
    }

    @Override
    public MovingParticleEffect read(ParticleType<MovingParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new MovingParticleEffect(type, moveVec);
    }

    @Override
    public MovingParticleEffect read(ParticleType<MovingParticleEffect> type, PacketByteBuf buf) {
        Vec3d moveVec = moveVecArg.read(buf);
        return new MovingParticleEffect(type, moveVec);
    }

    @Override
    public String write() {
        return moveVecArg.write();
    }

    @Override
    public Codec<MovingParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                moveVecArg.getCodec("moveVec")
                        .forGetter(MovingParticleEffect::getMoveVec)
        ).apply(instance, (moveVec) -> new MovingParticleEffect(type, moveVec)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        moveVecArg.write(buf);
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }
}
