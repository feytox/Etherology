package ru.feytox.etherology.particle.subtypes;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.info.*;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.particle.utility.ParticleInfoProvider;
import ru.feytox.etherology.util.misc.CodecUtil;

@RequiredArgsConstructor
public enum LightSubtype implements ParticleInfoProvider<LightParticle, LightParticleEffect>, StringIdentifiable {
    SIMPLE(LightSimpleInfo::new),
    SPARK(LightSparkInfo::new),
    PUSHING(LightPushingInfo::new),
    ATTRACT(LightAttractInfo::new),
    BREWING(LightBrewingInfo::new),
    MATRIX(LightMatrixInfo::new),
    GENERATOR(LightGeneratorInfo::new),
    HAZE(LightHazeInfo::new);

    public static final Codec<LightSubtype> CODEC = StringIdentifiable.createBasicCodec(LightSubtype::values);
    public static final PacketCodec<ByteBuf, LightSubtype> PACKET_CODEC = CodecUtil.ofEnum(values());

    @Nullable
    private final ParticleInfo.Factory<LightParticle, LightParticleEffect> infoFactory;

    @Override
    @Nullable
    public ParticleInfo.Factory<LightParticle, LightParticleEffect> getFactory() {
        return infoFactory;
    }

    @Override
    public String asString() {
        return name();
    }
}
