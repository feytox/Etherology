package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleType;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

public class ElectricityParticleEffect extends FeyParticleEffect<ElectricityParticleEffect> {

    @Getter
    private final ElectricitySubtype electricityType;

    private ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type, ElectricitySubtype electricityType) {
        super(type);
        this.electricityType = electricityType;
    }

    public ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type) {
        this(type, null);
    }

    @Override
    public MapCodec<ElectricityParticleEffect> createCodec() {
        return ElectricitySubtype.CODEC.xmap(factory(ElectricityParticleEffect::new), ElectricityParticleEffect::getElectricityType)
                .fieldOf("electricity_type");
    }

    @Override
    public PacketCodec<RegistryByteBuf, ElectricityParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(ElectricitySubtype.PACKET_CODEC, ElectricityParticleEffect::getElectricityType, factory(ElectricityParticleEffect::new));
    }

    public static ElectricityParticleEffect of(Random random, ElectricitySubtype electricityType) {
        return new ElectricityParticleEffect(getRandomType(random), electricityType);
    }

    public static FeyParticleType<ElectricityParticleEffect> getRandomType(Random random) {
        return random.nextBoolean() ? EtherParticleTypes.ELECTRICITY1 : EtherParticleTypes.ELECTRICITY2;
    }
}
