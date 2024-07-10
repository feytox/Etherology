package ru.feytox.etherology.particle.effects.misc;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

@Getter
public class FeyParticleType<T extends ParticleEffect> extends ParticleType<T> {

    private final MapCodec<T> codec;
    private final PacketCodec<RegistryByteBuf, T> packetCodec;

    public FeyParticleType(boolean alwaysShow, FeyParticleEffect.DummyConstructor<T> dummyConstructor) {
        super(alwaysShow);
        FeyParticleEffect<T> dummy = dummyConstructor.createDummy(this);
        codec = dummy.createCodec();
        packetCodec = dummy.createPacketCodec();
    }
}
