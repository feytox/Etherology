package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.effects.args.EnumArg;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public class SparkParticleEffect extends FeyParticleEffect<SparkParticleEffect> {

    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();
    private final ParticleArg<EssenceZoneType> zoneTypeArg = EnumArg.of(EssenceZoneType.class);

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type, Vec3d moveVec) {
        this(type, moveVec, EssenceZoneType.EMPTY);
    }

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type, Vec3d moveVec, EssenceZoneType zoneType) {
        this(type);
        moveVecArg.setValue(moveVec);
        zoneTypeArg.setValue(zoneType);
    }

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type) {
        super(type);
    }

    @Override
    public SparkParticleEffect read(ParticleType<SparkParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        EssenceZoneType zoneType = zoneTypeArg.read(reader);
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new SparkParticleEffect(type, moveVec, zoneType);
    }

    @Override
    public SparkParticleEffect read(ParticleType<SparkParticleEffect> type, PacketByteBuf buf) {
        EssenceZoneType zoneType = zoneTypeArg.read(buf);
        Vec3d moveVec = moveVecArg.read(buf);
        return new SparkParticleEffect(type, moveVec, zoneType);
    }

    @Override
    public String write() {
        return zoneTypeArg.write() + " " + moveVecArg.write();
    }

    @Override
    public Codec<SparkParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                moveVecArg.getCodec("moveVec")
                        .forGetter(SparkParticleEffect::getMoveVec),
                zoneTypeArg.getCodec("zoneType")
                        .forGetter(SparkParticleEffect::getZoneType)
        ).apply(instance, (moveVec, zoneType) -> new SparkParticleEffect(type, moveVec, zoneType)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        zoneTypeArg.write(buf);
        moveVecArg.write(buf);
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    public EssenceZoneType getZoneType() {
        return zoneTypeArg.getValue();
    }
}
