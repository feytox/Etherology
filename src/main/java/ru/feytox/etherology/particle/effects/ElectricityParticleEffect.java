package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.particle.effects.args.EnumArg;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleType;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;

public class ElectricityParticleEffect extends FeyParticleEffect<ElectricityParticleEffect> {

    private final ParticleArg<ElectricitySubtype> electricityTypeArg = EnumArg.of(ElectricitySubtype.class);

    private ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type, ElectricitySubtype electricityType) {
        this(type);
        electricityTypeArg.setValue(electricityType);
    }

    public ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type) {
        super(type);
    }

    public static ElectricityParticleEffect of(Random random, ElectricitySubtype electricityType) {
        return new ElectricityParticleEffect(getRandomType(random), electricityType);
    }

    @Override
    public ElectricityParticleEffect read(ParticleType<ElectricityParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        ElectricitySubtype electricityType = electricityTypeArg.read(reader);
        return new ElectricityParticleEffect(type, electricityType);
    }

    @Override
    public ElectricityParticleEffect read(ParticleType<ElectricityParticleEffect> type, PacketByteBuf buf) {
        ElectricitySubtype electricityType = electricityTypeArg.read(buf);
        return new ElectricityParticleEffect(type, electricityType);
    }

    @Override
    public String write() {
        return electricityTypeArg.write();
    }

    @Override
    public Codec<ElectricityParticleEffect> createCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                electricityTypeArg.getCodec("electricity_type").forGetter(ElectricityParticleEffect::getElectricityType)
        ).apply(instance, (electricityType) -> new ElectricityParticleEffect(type, electricityType)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        electricityTypeArg.write(buf);
    }

    public ElectricitySubtype getElectricityType() {
        return electricityTypeArg.getValue();
    }

    public static FeyParticleType<ElectricityParticleEffect> getRandomType(Random random) {
        return random.nextBoolean() ? ServerParticleTypes.ELECTRICITY1 : ServerParticleTypes.ELECTRICITY2;
    }
}
