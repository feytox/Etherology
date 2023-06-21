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

@Deprecated
public class TestParticleEffect extends FeyParticleEffect<TestParticleEffect> {
    private final EnumArg<LightParticleType> lightTypeArg = EnumArg.of(LightParticleType.class);
    private final ParticleArg<Vec3d> moveVecArg = SimpleArgs.VEC3D;

    public TestParticleEffect(ParticleType<TestParticleEffect> type, LightParticleType lightParticleType, Vec3d moveVec) {
        super(type);
        this.lightTypeArg.setValue(lightParticleType);
        this.moveVecArg.setValue(moveVec);
    }

    public TestParticleEffect(ParticleType<TestParticleEffect> type) {
        super(type);
    }

    public LightParticleType getLightType() {
        return lightTypeArg.getValue();
    }

    public Vec3d getMoveVec() {
        return moveVecArg.getValue();
    }

    @Override
    public TestParticleEffect read(ParticleType<TestParticleEffect> type, StringReader reader) throws CommandSyntaxException {
        reader.expect(' ');
        LightParticleType lightType = lightTypeArg.read(reader);
        reader.expect(' ');
        Vec3d moveVec = moveVecArg.read(reader);
        return new TestParticleEffect(type, lightType, moveVec);
    }

    @Override
    public TestParticleEffect read(ParticleType<TestParticleEffect> type, PacketByteBuf buf) {
        LightParticleType lightType = lightTypeArg.read(buf);
        Vec3d moveVec = moveVecArg.read(buf);
        return new TestParticleEffect(type, lightType, moveVec);
    }

    @Override
    public void write(PacketByteBuf buf) {
        lightTypeArg.write(buf);
        moveVecArg.write(buf);
    }

    @Override
    public String write() {
        return lightTypeArg.write() + " " + moveVecArg.write();
    }

    @Override
    public Codec<TestParticleEffect> createCodec() {
        return RecordCodecBuilder.create((instance) -> instance.group(
                lightTypeArg.getCodec("lightType")
                        .forGetter(TestParticleEffect::getLightType),
                moveVecArg.getCodec("moveVec")
                        .forGetter(TestParticleEffect::getMoveVec)
        ).apply(instance, (lightType, moveVec) -> new TestParticleEffect(type, lightType, moveVec)));
    }
}
