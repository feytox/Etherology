package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.subtype.SparkSubtype;
import ru.feytox.etherology.util.misc.CodecUtil;

@Getter
public class SparkParticleEffect extends FeyParticleEffect<SparkParticleEffect> {

    private final Vec3d moveVec;
    private final SparkSubtype sparkType;

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type, Vec3d moveVec, SparkSubtype sparkType) {
        super(type);
        this.moveVec = moveVec;
        this.sparkType = sparkType;
    }

    public SparkParticleEffect(ParticleType<SparkParticleEffect> type) {
        this(type, null, null);
    }

    @Override
    public MapCodec<SparkParticleEffect> createCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Vec3d.CODEC.fieldOf("moveVec").forGetter(SparkParticleEffect::getMoveVec),
                SparkSubtype.CODEC.fieldOf("sparkType").forGetter(SparkParticleEffect::getSparkType)
        ).apply(instance, biFactory(SparkParticleEffect::new)));
    }

    @Override
    public PacketCodec<RegistryByteBuf, SparkParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(CodecUtil.VEC3D_PACKET, SparkParticleEffect::getMoveVec,
                SparkSubtype.PACKET_CODEC, SparkParticleEffect::getSparkType, biFactory(SparkParticleEffect::new));
    }


}
