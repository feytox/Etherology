package ru.feytox.etherology.particle.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import ru.feytox.etherology.enums.EssenceZoneType;
import ru.feytox.etherology.particle.types.args.EnumArg;
import ru.feytox.etherology.particle.types.args.ParticleArg;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;

public class ZoneParticleEffect extends FeyParticleEffect<ZoneParticleEffect> {

    private final ParticleArg<EssenceZoneType> zoneTypeArg = EnumArg.of(EssenceZoneType.class);

    public ZoneParticleEffect(ParticleType<ZoneParticleEffect> type, EssenceZoneType zoneType) {
        super(type);
        this.zoneTypeArg.setValue(zoneType);
    }

    public ZoneParticleEffect(ParticleType<ZoneParticleEffect> type) {
        super(type);
    }

    @Override
    public ZoneParticleEffect read(ParticleType<ZoneParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        return new ZoneParticleEffect(type, zoneTypeArg.read(reader));
    }

    @Override
    public ZoneParticleEffect read(ParticleType<ZoneParticleEffect> type, PacketByteBuf buf) {
        return new ZoneParticleEffect(type, zoneTypeArg.read(buf));
    }

    @Override
    public String write() {
        return zoneTypeArg.write();
    }

    @Override
    public Codec<ZoneParticleEffect> createCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                zoneTypeArg.getCodec("zone_type").forGetter(ZoneParticleEffect::getZoneType)
        ).apply(instance, (zoneType) -> new ZoneParticleEffect(type, zoneType)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        zoneTypeArg.write(buf);
    }

    public EssenceZoneType getZoneType() {
        return zoneTypeArg.getValue();
    }
}
