package ru.feytox.etherology.particle.effects;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.util.misc.CodecUtil;

public class MovingParticleEffect extends FeyParticleEffect<MovingParticleEffect> {

    @Getter
    private final Vec3d moveVec;

    public MovingParticleEffect(ParticleType<MovingParticleEffect> type, Vec3d moveVec) {
        super(type);
        this.moveVec = moveVec;
    }

    public MovingParticleEffect(ParticleType<MovingParticleEffect> type) {
        this(type, null);
    }

    @Override
    public MapCodec<MovingParticleEffect> createCodec() {
        return Vec3d.CODEC.xmap(factory(MovingParticleEffect::new), MovingParticleEffect::getMoveVec).fieldOf("moveVec");
    }

    @Override
    public PacketCodec<RegistryByteBuf, MovingParticleEffect> createPacketCodec() {
        return PacketCodec.tuple(CodecUtil.VEC3D_PACKET, MovingParticleEffect::getMoveVec, factory(MovingParticleEffect::new));
    }
}
