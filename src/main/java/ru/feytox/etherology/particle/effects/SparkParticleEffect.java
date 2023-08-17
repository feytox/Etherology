package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.args.EnumArg;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.subtypes.SparkSubtype;

public class SparkParticleEffect extends FeyParticleEffect<SparkParticleEffect> {

    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();
    private final ParticleArg<SparkSubtype> sparkTypeArg = EnumArg.of(SparkSubtype.class);

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type, Vec3d moveVec, SparkSubtype sparkType) {
        super(type);
        moveVecArg.setValue(moveVec);
        sparkTypeArg.setValue(sparkType);
    }

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type) {
        super(type);
    }

    @Override
    public SparkParticleEffect read(ParticleType<SparkParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        SparkSubtype sparkType = sparkTypeArg.read(reader);
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new SparkParticleEffect(type, moveVec, sparkType);
    }

    @Override
    public SparkParticleEffect read(ParticleType<SparkParticleEffect> type, PacketByteBuf buf) {
        SparkSubtype sparkType = sparkTypeArg.read(buf);
        Vec3d moveVec = moveVecArg.read(buf);
        return new SparkParticleEffect(type, moveVec, sparkType);
    }

    @Override
    public String write() {
        return sparkTypeArg.write() + " " + moveVecArg.write();
    }

    @Override
    public Codec<SparkParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                moveVecArg.getCodec("moveVec")
                        .forGetter(SparkParticleEffect::getMoveVec),
                sparkTypeArg.getCodec("sparkType")
                        .forGetter(SparkParticleEffect::getSparkType))
                .apply(instance, (moveVec, sparkType) -> new SparkParticleEffect(type, moveVec, sparkType)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        sparkTypeArg.write(buf);
        moveVecArg.write(buf);
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    public SparkSubtype getSparkType() {
        return sparkTypeArg.getValue();
    }
}
