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

public class ScalableParticleEffect extends FeyParticleEffect<ScalableParticleEffect> {

    private final ParticleArg<Float> scaleArg = SimpleArgs.FLOAT.get();

    public ScalableParticleEffect(ParticleType<ScalableParticleEffect> type, float scale) {
        this(type);
        scaleArg.setValue(scale);
    }

    public ScalableParticleEffect(ParticleType<ScalableParticleEffect> type) {
        super(type);
    }

    @Override
    public ScalableParticleEffect read(ParticleType<ScalableParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        float scale = scaleArg.read(reader);
        return new ScalableParticleEffect(type, scale);
    }

    @Override
    public ScalableParticleEffect read(ParticleType<ScalableParticleEffect> type, PacketByteBuf buf) {
        float scale = scaleArg.read(buf);
        return new ScalableParticleEffect(type, scale);
    }

    @Override
    public String write() {
        return scaleArg.write();
    }

    @Override
    public Codec<ScalableParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                scaleArg.getCodec("scale")
                        .forGetter(ScalableParticleEffect::getScale)
        ).apply(instance, (scale) -> new ScalableParticleEffect(type, scale)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        scaleArg.write(buf);
    }

    public Float getScale() {
        return scaleArg.getValue();
    }
}
