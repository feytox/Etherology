package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleType;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public class ScalableParticleEffect extends FeyParticleEffect<ScalableParticleEffect> {

    @Getter
    private final Float scale;

    public ScalableParticleEffect(ParticleType<ScalableParticleEffect> type, Float scale) {
        super(type);
        this.scale = scale;
    }

    public ScalableParticleEffect(ParticleType<ScalableParticleEffect> type) {
        this(type, null);
    }

    @Override
    public MapCodec<ScalableParticleEffect> createCodec() {
        return Codec.FLOAT.xmap(factory(ScalableParticleEffect::new), ScalableParticleEffect::getScale).fieldOf("scale");
    }

    @Override
    public PacketCodec<RegistryByteBuf, ScalableParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(PacketCodecs.FLOAT, ScalableParticleEffect::getScale, factory(ScalableParticleEffect::new));
    }

}
