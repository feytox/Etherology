package ru.feytox.etherology.particle.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.enums.LightParticleType;
import ru.feytox.etherology.particle.types.args.EnumArg;
import ru.feytox.etherology.particle.types.args.ParticleArg;
import ru.feytox.etherology.particle.types.args.SimpleArgs;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;

public class LightParticleEffect extends FeyParticleEffect<LightParticleEffect> {

    private final ParticleArg<LightParticleType> lightTypeArg = EnumArg.of(LightParticleType.class);
    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D.get();

    public LightParticleEffect(ParticleType<LightParticleEffect> type, LightParticleType lightType, Vec3d moveVec) {
        super(type);
        lightTypeArg.setValue(lightType);
        moveVecArg.setValue(moveVec);
    }

    public LightParticleEffect(ParticleType<LightParticleEffect> type) {
        super(type);
    }

    public LightParticleType getLightType() {
        return lightTypeArg.getValue();
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    @Override
    public LightParticleEffect read(ParticleType<LightParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        LightParticleType lightType = lightTypeArg.read(reader);
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new LightParticleEffect(type, lightType, moveVec);
    }

    @Override
    public LightParticleEffect read(ParticleType<LightParticleEffect> type, PacketByteBuf buf) {
        LightParticleType lightType = lightTypeArg.read(buf);
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
