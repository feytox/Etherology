package ru.feytox.etherology.particle.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.types.args.ParticleArg;
import ru.feytox.etherology.particle.types.args.SimpleArgs;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;

public class SteamParticleEffect extends FeyParticleEffect<SteamParticleEffect> {

    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();
    private final ParticleArg<Double> percentArg = SimpleArgs.DOUBLE.get();

    public SteamParticleEffect(ParticleType<SteamParticleEffect> type, Vec3d moveVec, double percent) {
        super(type);
        moveVecArg.setValue(moveVec);
        percentArg.setValue(percent);
    }

    public SteamParticleEffect(ParticleType<SteamParticleEffect> type) {
        super(type);
    }

    @Override
    public SteamParticleEffect read(ParticleType<SteamParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        reader.expect(' ');
        double percent = percentArg.read(reader);
        return new SteamParticleEffect(type, moveVec, percent);
    }

    @Override
    public SteamParticleEffect read(ParticleType<SteamParticleEffect> type, PacketByteBuf buf) {
        Vec3d moveVec = moveVecArg.read(buf);
        double percent = percentArg.read(buf);
        return new SteamParticleEffect(type, moveVec, percent);
    }

    @Override
    public String write() {
        return moveVecArg.write() + ' ' + percentArg.write();
    }

    @Override
    public Codec<SteamParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                moveVecArg.getCodec("moveVec")
                        .forGetter(SteamParticleEffect::getMoveVec),
                percentArg.getCodec("percent")
                        .forGetter(SteamParticleEffect::getPercent)
        ).apply(instance, (moveVec, percent) -> new SteamParticleEffect(type, moveVec, percent)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        moveVecArg.write(buf);
        percentArg.write(buf);
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    public Double getPercent() {
        return percentArg.getValue();
    }
}
