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

public class ZoneParticleEffect extends FeyParticleEffect<ZoneParticleEffect> {

    private final ParticleArg<EssenceZoneType> zoneTypeArg = EnumArg.of(EssenceZoneType.class);
    private final ParticleArg<Vec3d> endPosArg = SimpleArgs.VEC3D.get();

    public ZoneParticleEffect(ParticleType<ZoneParticleEffect> type, EssenceZoneType zoneType, Vec3d endPos) {
        super(type);
        zoneTypeArg.setValue(zoneType);
        endPosArg.setValue(endPos);
    }

    public ZoneParticleEffect(ParticleType<ZoneParticleEffect> type) {
        super(type);
    }

    @Override
    public ZoneParticleEffect read(ParticleType<ZoneParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        EssenceZoneType zoneType = zoneTypeArg.read(reader);
        reader.expect(' ');
        Vec3d endPos = endPosArg.read(reader);
        return new ZoneParticleEffect(type, zoneType, endPos);
    }

    @Override
    public ZoneParticleEffect read(ParticleType<ZoneParticleEffect> type, PacketByteBuf buf) {
        return new ZoneParticleEffect(type, zoneTypeArg.read(buf), endPosArg.read(buf));
    }

    @Override
    public String write() {
        return zoneTypeArg.write() + " " + endPosArg.write();
    }

    @Override
    public Codec<ZoneParticleEffect> createCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                zoneTypeArg.getCodec("zone_type").forGetter(ZoneParticleEffect::getZoneType),
                endPosArg.getCodec("end_pos").forGetter(ZoneParticleEffect::getEndPos)
        ).apply(instance, (zoneType, endPos) -> new ZoneParticleEffect(type, zoneType, endPos)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        zoneTypeArg.write(buf);
        endPosArg.write(buf);
    }

    public EssenceZoneType getZoneType() {
        return zoneTypeArg.getValue();
    }

    public Vec3d getEndPos() {
        return endPosArg.getValue();
    }
}
