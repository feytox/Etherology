package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.util.misc.CodecUtil;

@Getter
public class ZoneParticleEffect extends FeyParticleEffect<ZoneParticleEffect> {

    private final EssenceZoneType zoneType;
    private final Vec3d endPos;

    public ZoneParticleEffect(ParticleType<ZoneParticleEffect> type, EssenceZoneType zoneType, Vec3d endPos) {
        super(type);
        this.zoneType = zoneType;
        this.endPos = endPos;
    }

    public ZoneParticleEffect(ParticleType<ZoneParticleEffect> type) {
        this(type, null, null);
    }

    @Override
    public MapCodec<ZoneParticleEffect> createCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                EssenceZoneType.CODEC.fieldOf("zoneType").forGetter(ZoneParticleEffect::getZoneType),
                Vec3d.CODEC.fieldOf("endPos").forGetter(ZoneParticleEffect::getEndPos)
        ).apply(instance, biFactory(ZoneParticleEffect::new)));
    }

    @Override
    public PacketCodec<RegistryByteBuf, ZoneParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(EssenceZoneType.PACKET_CODEC, ZoneParticleEffect::getZoneType,
                CodecUtil.VEC3D_PACKET, ZoneParticleEffect::getEndPos, biFactory(ZoneParticleEffect::new));
    }
}
