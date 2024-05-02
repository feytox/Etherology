package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public class LightningBoltParticleEffect extends FeyParticleEffect<LightningBoltParticleEffect> {

    private final ParticleArg<Float> scaleArg = SimpleArgs.FLOAT.get();

    public LightningBoltParticleEffect(ParticleType<LightningBoltParticleEffect> type, float scale) {
        this(type);
        scaleArg.setValue(scale);
    }

    public LightningBoltParticleEffect(ParticleType<LightningBoltParticleEffect> type) {
        super(type);
    }

    @Override
    public LightningBoltParticleEffect read(ParticleType<LightningBoltParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        float scale = scaleArg.read(reader);
        return new LightningBoltParticleEffect(type, scale);
    }

    @Override
    public LightningBoltParticleEffect read(ParticleType<LightningBoltParticleEffect> type, PacketByteBuf buf) {
        float scale = scaleArg.read(buf);
        return new LightningBoltParticleEffect(type, scale);
    }

    @Override
    public String write() {
        return scaleArg.write();
    }

    @Override
    public Codec<LightningBoltParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                scaleArg.getCodec("scale")
                        .forGetter(LightningBoltParticleEffect::getScale)
        ).apply(instance, (scale) -> new LightningBoltParticleEffect(type, scale)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        scaleArg.write(buf);
    }

    public Float getScale() {
        return scaleArg.getValue();
    }
}
