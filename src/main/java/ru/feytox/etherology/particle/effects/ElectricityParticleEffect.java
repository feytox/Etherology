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
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleType;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;

public class ElectricityParticleEffect extends FeyParticleEffect<ElectricityParticleEffect> {

    private final ParticleArg<ElectricitySubtype> electricityTypeArg = EnumArg.of(ElectricitySubtype.class);
    private final ParticleArg<Float> instabilityArg = SimpleArgs.FLOAT.get();


    public ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type, ElectricitySubtype electricityType) {
        this(type, electricityType, -1.0f);
    }

    public ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type, ElectricitySubtype electricityType, Float instability) {
        this(type);
        electricityTypeArg.setValue(electricityType);
        instabilityArg.setValue(instability);
    }

    public ElectricityParticleEffect(ParticleType<ElectricityParticleEffect> type) {
        super(type);
    }

    @Override
    public ElectricityParticleEffect read(ParticleType<ElectricityParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        ElectricitySubtype electricityType = electricityTypeArg.read(reader);
        reader.expect(' ');
        float instability = instabilityArg.read(reader);
        return new ElectricityParticleEffect(type, electricityType, instability);
    }

    @Override
    public ElectricityParticleEffect read(ParticleType<ElectricityParticleEffect> type, PacketByteBuf buf) {
        ElectricitySubtype electricityType = electricityTypeArg.read(buf);
        float instability = instabilityArg.read(buf);
        return new ElectricityParticleEffect(type, electricityType, instability);
    }

    @Override
    public String write() {
        return electricityTypeArg.write() + " " + instabilityArg.write();
    }

    @Override
    public Codec<ElectricityParticleEffect> createCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                electricityTypeArg.getCodec("electricity_type").forGetter(ElectricityParticleEffect::getElectricityType),
                instabilityArg.getCodec("instability").forGetter(ElectricityParticleEffect::getInstability)
        ).apply(instance, (electricityType, instability) -> new ElectricityParticleEffect(type, electricityType, instability)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        electricityTypeArg.write(buf);
        instabilityArg.write(buf);
    }

    public ElectricitySubtype getElectricityType() {
        return electricityTypeArg.getValue();
    }

    public Float getInstability() {
        return instabilityArg.getValue();
    }

    public static FeyParticleType<ElectricityParticleEffect> getRandomType(Random random) {
        return random.nextBoolean() ? ServerParticleTypes.ELECTRICITY1 : ServerParticleTypes.ELECTRICITY2;
    }
}
