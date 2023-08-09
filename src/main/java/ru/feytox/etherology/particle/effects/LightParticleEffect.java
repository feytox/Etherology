package ru.feytox.etherology.particle.effects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.args.EnumArg;
import ru.feytox.etherology.particle.effects.args.ParticleArg;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.subtypes.LightSubtype;

public class LightParticleEffect extends FeyParticleEffect<LightParticleEffect> {

    private final ParticleArg<LightSubtype> lightTypeArg = EnumArg.of(LightSubtype.class);
    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();

    public LightParticleEffect(ParticleType<LightParticleEffect> type, LightSubtype lightType, Vec3d moveVec) {
        super(type);
        lightTypeArg.setValue(lightType);
        moveVecArg.setValue(moveVec);
    }

    public LightParticleEffect(ParticleType<LightParticleEffect> type) {
        super(type);
    }

    public LightSubtype getLightType() {
        return lightTypeArg.getValue();
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    @Override
    public LightParticleEffect read(ParticleType<LightParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        LightSubtype lightType = lightTypeArg.read(reader);
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new LightParticleEffect(type, lightType, moveVec);
    }

    @Override
    public LightParticleEffect read(ParticleType<LightParticleEffect> type, PacketByteBuf buf) {
        LightSubtype lightType = lightTypeArg.read(buf);
        Vec3d moveVec = moveVecArg.read(buf);
        return new LightParticleEffect(type, lightType, moveVec);
    }

    @Override
    public String write() {
        return lightTypeArg.write() + " " + moveVecArg.write();
    }

    @Override
    public Codec<LightParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                lightTypeArg.getCodec("lightType")
                        .forGetter(LightParticleEffect::getLightType),
                moveVecArg.getCodec("moveVec")
                        .forGetter(LightParticleEffect::getMoveVec)
        ).apply(instance, (lightType, moveVec) -> new LightParticleEffect(type, lightType, moveVec)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        lightTypeArg.write(buf);
        moveVecArg.write(buf);
    }
}
