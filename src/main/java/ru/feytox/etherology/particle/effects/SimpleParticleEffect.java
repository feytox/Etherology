package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.util.misc.CodecUtil;

public class SimpleParticleEffect extends FeyParticleEffect<SimpleParticleEffect> {

    public SimpleParticleEffect(ParticleType<SimpleParticleEffect> type) {
        super(type);
    }

    @Override
    public MapCodec<SimpleParticleEffect> createCodec() {
        return MapCodec.unit(new SimpleParticleEffect(type));
    }

    @Override
    public PacketCodec<RegistryByteBuf, SimpleParticleEffect> createPacketCodec() {
        return CodecUtil.unitUnchecked(new SimpleParticleEffect(type));
    }
}
