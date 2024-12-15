package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.subtype.LightSubtype;
import ru.feytox.etherology.util.misc.CodecUtil;

@Getter
public class LightParticleEffect extends FeyParticleEffect<LightParticleEffect> {

    private final LightSubtype lightType;
    private final Vec3d moveVec;

    public LightParticleEffect(ParticleType<LightParticleEffect> type, LightSubtype lightType, Vec3d moveVec) {
        super(type);
        this.lightType = lightType;
        this.moveVec = moveVec;
    }

    public LightParticleEffect(ParticleType<LightParticleEffect> type) {
        this(type, null, null);
    }

    @Override
    public MapCodec<LightParticleEffect> createCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                LightSubtype.CODEC.fieldOf("lightType").forGetter(LightParticleEffect::getLightType),
                Vec3d.CODEC.fieldOf("moveVec").forGetter(LightParticleEffect::getMoveVec)
        ).apply(instance, biFactory(LightParticleEffect::new)));
    }

    @Override
    public PacketCodec<RegistryByteBuf, LightParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(LightSubtype.PACKET_CODEC, LightParticleEffect::getLightType,
                CodecUtil.VEC3D_PACKET, LightParticleEffect::getMoveVec, biFactory(LightParticleEffect::new));
    }
}
